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
 *             collections:
 *               type: array
 *               items:
 *                 type: object
 *                 properties:
 *                   id:
 *                     type: integer
 *                   name:
 *                     type: string
 *                   introduction:
 *                     type: string
 *             admin:
 *               type: boolean
 *             feedbacks:
 *               type: array
 *               items:
 *                 type: object
 *     UserInfoWithToken:
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
 *             collections:
 *               type: array
 *               items:
 *                 type: object
 *                 properties:
 *                   id:
 *                     type: integer
 *                   name:
 *                     type: string
 *                   introduction:
 *                     type: string
 *             admin:
 *               type: boolean
 *             feedbacks:
 *               type: array
 *               items:
 *                 type: object
 *             token:
 *               type: string
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
import crypto from "crypto";

const router: Router = express.Router();

const db = new PrismaClient();

const secretKey = process.env.SECRET_KEY || "software";

const hash = (value: string) =>
    parseInt(crypto.createHash("md5").update(value).digest("hex"), 16);

// interface JwtUser {
//     id: number;
//     name: string;
//     admin: boolean;
// }

function jwtSign(user: {
    id: number;
    admin: boolean;
    name: string;
    avatar: string;
    [key: string]: any;
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

const UserInfoWithoutPassword = {
    id: true,
    name: true,
    avatar: true,
    collections: {
        select: {
            id: true,
            name: true,
            introduction: true,
        },
    },
    admin: true,
    feedbacks: true,
};

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
 *          $ref: '#/components/schemas/UserInfoWithToken'
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
        select: UserInfoWithoutPassword,
    });
    if (user === null) {
        res.status(401).json({
            error: "用户名或密码不正确",
        });
        return;
    }

    // 剔除 password，添加 token，然后返回
    res.json({
        ...user,
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
 *           $ref: '#/components/schemas/UserInfoWithToken'
 *       400:
 *         description: 用户名已注册 / 参数类型错误
 *         content:
 *           $ref: '#/components/schemas/Error'
 */
router.post("/register", async (req, res) => {
    const RegisterInfo = z.object({ name: z.string(), password: z.string() });
    const { name, password } = RegisterInfo.parse(req.body);

    const defaultAvatars = [
        "big-husk-face.png",
        "big-slime-face.png",
        "big-steve-face.png",
        "big-zombie-face.png",
        "big-mooshroom-face.png",
        "big-snowgolem-face.png",
        "big-vex-face.png",
    ];
    // 从默认头像中根据用户名哈希值选择一个作为用户的头像
    // prrttier-ignore
    const avatar = `/avatars/default_avatars/${defaultAvatars[Math.abs(hash(name) % defaultAvatars.length)]}`;

    try {
        const user = await db.user.create({
            data: { name, password, avatar },
            // 剔除 password
            select: UserInfoWithoutPassword,
        });

        // 添加 token，然后返回
        res.json({
            ...user,
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

/**
 * @openapi
 * /user/profile/{id}:
 *   get:
 *     summary: 获取用户信息
 *     description: "获取自己指或定用户的信息（若不提供id，则获取自己的信息；提供则获取指定id的用户的信息）。<br />需要登陆。"
 *     tags: [User]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: false
 *         description: 用户的 ID
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: 成功获取用户信息
 *         content:
 *           $ref: '#/components/schemas/UserInfo'
 *       404:
 *         description: 用户不存在
 *         content:
 *           $ref: '#/components/schemas/Error'
 * @requiresAuth
 */
router.get("/profile", (req, res) => {
    const userId = req.auth.id;

    db.user
        .findUnique({
            where: { id: userId },
            // select 时不包含 password
            select: UserInfoWithoutPassword,
        })
        .then((user) => {
            if (user === null) {
                res.status(404).json({
                    error: "用户不存在",
                });
                return;
            }

            res.json(user);
        });
});

router.get("/profile/:id", (req, res) => {
    const userId = z.number().parse(parseInt(req.params.id));

    db.user
        .findUnique({
            where: { id: userId },
            // select 时不包含 password
            select: UserInfoWithoutPassword,
        })
        .then((user) => {
            if (user === null) {
                res.status(404).json({
                    error: "用户不存在",
                });
                return;
            } else res.json(user);
        });
});

/**
 * @openapi
 * /user/collect/{id}:
 *   get:
 *     summary: 收藏知识条目
 *     description: "收藏知识条目。<br />需要登陆。"
 *     tags: [User]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: 知识条目的 ID
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: 收藏成功（注意，如果条目已被收藏，也会返回 200）
 *       404:
 *         description: 指定的条目不存在
 * @requiresAuth
 */
router.get("/collect/:id", (req, res) => {
    const id = z.number().parse(parseInt(req.params.id));
    const userId = req.auth.id;

    db.user
        .update({
            where: { id: userId },
            data: {
                collections: {
                    connect: { id },
                },
            },
        })
        .then(() => {
            res.sendStatus(200);
        })
        .catch((err) => {
            res.status(404).json({
                error: "指定的条目不存在",
            });
        });
});

/**
 * @openapi
 * /user/uncollect/{id}:
 *   get:
 *     summary: 取消收藏知识条目
 *     description: "取消收藏指定知识条目。<br />需要登陆。"
 *     tags: [User]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: 知识条目的 ID
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: 取消收藏成功（注意，如果条目本身未被收藏，也会返回 200）
 *       404:
 *         description: 指定的条目不存在
 * @requiresAuth
 */
router.get("/uncollect/:id", (req, res) => {
    const id = z.number().parse(parseInt(req.params.id));
    const userId = req.auth.id;

    db.user
        .update({
            where: { id: userId },
            data: {
                collections: {
                    disconnect: { id },
                },
            },
        })
        .then(() => {
            res.sendStatus(200);
        })
        .catch((err) => {
            res.status(404).json({
                error: "指定的条目不存在",
            });
        });
});

export default router;
