/**
 * @openapi
 * tags:
 *   name: Admin
 *   description: 管理相关接口
 */

import express, { Router } from "express";
import { expressjwt } from "express-jwt";
import { z } from "Zod";
import { Prisma, PrismaClient } from "@prisma/client";

const router: Router = express.Router();

const db = new PrismaClient();

const secretKey = process.env.SECRET_KEY || "software";

router.use(
    expressjwt({ secret: secretKey, algorithms: ["HS256"] })
);

router.use((req, res, next) => {
    if (req.auth.admin) {
        next();
    } else {
        res.status(403).json({
            error: "无权访问此接口，需要管理员权限。",
        });
    }
})


/**
 * @openapi
 * /admin/createEntry:
 *   post:
 *     summary: 创建新的知识条目
 *     tags: [Admin]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               name:
 *                 type: string
 *               introduction:
 *                 type: string
 *               content:
 *                 type: string
 *     responses:
 *       200:
 *         description: 成功创建知识条目
 *       400:
 *         description: 参数类型错误
 */
router.post("/createEntry", async (req, res) => {
    try {
        z.object({
            name: z.string(),
            introduction: z.string(),
            content: z.string(),
        }).parse(req.body);
    } catch (e) {
        res.status(400).json({
            error: "参数类型错误",
        });
        return;
    }

    const { name, introduction, content } = req.body;

    try {
        await db.entry.create({
            data: {
                name,
                introduction,
                content,
            }
        });
        res.status(200).json({
            message: "成功创建知识条目",
        });
    } catch (e) {
        console.error(e);
        res.status(500).json({
            error: "服务器内部错误",
        });
    }
});


/**
 * @openapi
 * /admin/deleteEntry:
 *   post:
 *     summary: 删除指定 ID 的知识条目
 *     tags: [Admin]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               id:
 *                 type: integer
 *     responses:
 *       200:
 *         description: 成功删除知识条目
 *       400:
 *         description: 参数类型错误
 */
router.post("/deleteEntry", async (req, res) => {
    try {
        z.object({
            id: z.number(),
        }).parse(req.body).id;
    } catch (e) {
        res.status(400).json({
            error: "参数类型错误",
        });
        return;
    }

    const { id } = req.body;

    try {
        await db.entry.delete({
            where: { id },
        });
        res.status(200).json({
            message: "成功删除知识条目",
        });
    } catch (e) {
        console.error(e);
        res.status(500).json({
            error: "服务器内部错误",
        });
    }
});


/**
 * @openapi
 * /admin/updateEntry:
 *   post:
 *     summary: 更新指定 ID 的知识条目
 *     description: 可以更新知识条目的名称、简介和内容。必须指定 id，但其他字段是非必须的，只有提供了的字段会被更新。
 *     tags: [Admin]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               id:
 *                 type: integer
 *                 required: true
 *               name:
 *                 type: string
 *                 required: false
 *               introduction:
 *                 type: string
 *                 required: false
 *               content:
 *                 type: string
 *                 required: false
 *     responses:
 *       200:
 *         description: 成功更新知识条目
 *       400:
 *         description: 参数类型错误
 */
router.post("/updateEntry", async (req, res) => {
    try {
        z.object({
            id: z.number(),
            name: z.string().optional(),
            introduction: z.string().optional(),
            content: z.string().optional(),
        }).parse(req.body);
    } catch (e) {
        res.status(400).json({
            error: "参数类型错误",
        });
        return;
    }

    const { id, name, introduction, content } = req.body;

    try {
        await db.entry.update({
            where: { id },
            data: {
                name,
                introduction,
                content,
            }
        });
        res.status(200).json({
            message: "成功更新知识条目",
        });
    } catch (e) {
        console.error(e);
        res.status(500).json({
            error: "服务器内部错误",
        });
    }
});

export default router;