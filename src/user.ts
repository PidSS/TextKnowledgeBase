/**
 * @openapi
 * tags:
 *   name: User
 *   description: 用户相关接口
 * components:
 *   schemas:
 *     Error:
 *       application/json:
 *         schema:
 *           type: object
 *           properties:
 *             error:
 *               type: string
 *     UserInfo:
 *       application/json:
 *         schema:
 *           type: object
 *           properties:
 *             id:
 *               type: integer
 *             name:
 *               type: string
 *             avatar:
 *               type: string
 *             token:
 *               type: string
 *               description: JWT token
 *     LoginInfo:
 *       application/json:
 *         schema:
 *           type: object
 *           properties:
 *             name:
 *               type: string
 *             password:
 *               type: string
 */

import express, { Router } from "express";
import jwt from "jsonwebtoken";
import { expressjwt } from "express-jwt";
import { z } from "Zod";
import { Prisma, PrismaClient } from "@prisma/client";

const router: Router = express.Router();

const db = new PrismaClient();

const secretKey = process.env.SECRET_KEY || "software";

function jwtSign(user: {
    id: number;
    admin: boolean;
    name: string;
    password: string;
    avatar: string;
}): string {
    return jwt.sign(
        {
            id: user.id,
            name: user.name,
            admin: user.admin,
        },
        secretKey
    );
}

router.use(
    /\/(collect|uncollect|profile)/,
    expressjwt({ secret: secretKey, algorithms: ["HS256"] })
);

/**
 * @openapi
 * /user/login:
 *   post:
 *     summary: 用户登录
 *     description: "用户登录接口，使用用户名和密码登录，返回用户信息，包含 JWT token。访问需鉴权的接口时，请在 Headers 中添加 Authorization: Bearer &lt;token&gt;。"
 *     tags: [User]
 *     requestBody:
 *       required: true
 *       content:
 *         $ref: '#/components/schemas/LoginInfo'
 *     responses:
 *       200:
 *        description: 登录成功
 *        content:
 *          $ref: '#/components/schemas/UserInfo'
 *       401:
 *         description: 用户名或密码不正确
 *         content:
 *           $ref: '#/components/schemas/Error'
 *       400:
 *         description: 参数类型错误
 *         content:
 *           $ref: '#/components/schemas/Error'
 */
router.post("/login", async (req, res) => {
    const LoginInfo = z.object({ name: z.string(), password: z.string() });
    const { name, password } = LoginInfo.parse(req.body);

    const user = await db.user.findUnique({
        where: { name, password },
    });
    if (user === null) {
        res.status(401).json({
            error: "用户名或密码不正确",
        });
        return;
    }

    // 剔除 password，添加 token，然后返回
    const { password: _, ...result } = user;
    res.json({
        ...result,
        token: jwtSign(user),
    });
});


/**
 * @openapi
 * /user/register:
 *   post:
 *     summary: 用户注册
 *     description: "用户注册接口，使用用户名和密码注册。注册成功后返回用户信息，包含 JWT token。"
 *     tags: [User]
 *     requestBody:
 *       required: true
 *       content:
 *         $ref: '#/components/schemas/LoginInfo'
 *     responses:
 *       200:
 *         description: 注册成功
 *         content:
 *           $ref: '#/components/schemas/UserInfo'
 *       400:
 *         description: 用户名已注册 / 参数类型错误
 *         content:
 *           $ref: '#/components/schemas/Error'
 */
router.post("/register", async (req, res) => {
    const RegisterInfo = z.object({ name: z.string(), password: z.string() });
    const { name, password } = RegisterInfo.parse(req.body);

    try {
        const user = await db.user.create({
            data: {
                name,
                password,
                avatar: "/avatars/default_avatars/big-steve-face.png",
            },
        });

        // 剔除 password，添加 token，然后返回
        const { password: _, ...result } = user;
        res.json({
            ...result,
            token: jwtSign(user),
        });
    } catch (error) {
        if (error instanceof Prisma.PrismaClientKnownRequestError) {
            if (error.code === "P2002") {
                // 唯一约束冲突
                res.status(400).json({
                    error: "用户名与已注册，请尝试其他用户名",
                });
                return;
            }
        }
        throw error;
    }
});

router.get("/profile");

router.get("/collect/:id");

router.get("/uncollect/:id");

export default router;
