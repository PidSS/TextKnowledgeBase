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
