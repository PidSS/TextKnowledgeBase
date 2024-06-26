/**
 * @openapi
 * tags:
 *   name: Feedback
 *   description: 用户反馈相关接口
 * components:
 *   schemas:
 *     Error:
 *       application/json:
 *         schema:
 *           type: object
 *           properties:
 *             error:
 *               type: string
 *     Feedback:
 *       application/json:
 *         schema:
 *           type: object
 *           properties:
 *             id:
 *               type: integer
 *             entryId:
 *               type: integer
 *             userId:
 *               type: integer
 *             description:
 *               type: string
 *             response:
 *               type: string
 *             solved:
 *               type: boolean
 */

import express, { Router } from "express";
import { expressjwt } from "express-jwt";
import { z } from "Zod";
import { PrismaClient } from "@prisma/client";

const router: Router = express.Router();

const db = new PrismaClient();

const secretKey = process.env.SECRET_KEY || "software";

router.use(expressjwt({ secret: secretKey, algorithms: ["HS256"] }));

router.use(/\/(\d+|list|update|delete)/, (req, res, next) => {
    if (req.auth.admin) {
        next();
    } else {
        res.status(403).json({
            error: "无权访问此接口，需要管理员权限。",
        });
    }
});


/**
 * @openapi
 * /feedback:
 *   post:
 *     summary: 用户提交反馈
 *     description: 针对条目提交反馈，需要登陆。
 *     tags: [Feedback]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               entryId:
 *                 type: integer
 *               description:
 *                 type: string
 *     responses:
 *       200:
 *         description: 成功提交反馈
 *         content:
 *           $ref: '#/components/schemas/Feedback'
 *       404:
 *         description: 无对应条目
 *         content:
 *           $ref: '#/components/schemas/Error'
 */
router.post("/", (req, res) => {
    const { entryId, description } = z
        .object({
            entryId: z.number(),
            description: z.string(),
        })
        .parse(req.body);
    const { id: userId } = req.auth;

    db.feedback
        .create({
            data: {
                entry: {
                    connect: { id: entryId },
                },
                user: {
                    connect: { id: userId },
                },
                description,
            },
        })
        .then((feedback) => {
            res.json(feedback);
        })
        .catch((error) => {
            res.status(404).json({
                error: "无对应条目，请检查 entryId",
            });
        });
});


/**
 * @openapi
 * /feedback/my:
 *   get:
 *     summary: 获取当前用户的所有反馈
 *     description: 获取当前用户的所有反馈，需要登陆。
 *     tags: [Feedback]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: 获取成功
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/Feedback'
 */
router.get("/my", async (req, res) => {
    const { id: userId } = req.auth;

    const feedbacks = await db.feedback.findMany({
        where: {
            userId,
        },
    });
    res.json(feedbacks);
});


/**
 * @openapi
 * /feedback/{id}:
 *   get:
 *     summary: 获取反馈 <admin>
 *     description: 根据反馈 ID 获取反馈。需要管理员权限。
 *     tags: [Feedback]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: 反馈 ID
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: 获取成功
 *         content:
 *           $ref: '#/components/schemas/Feedback'
 *       404:
 *         description: 无对应反馈
 *         content:
 *           $ref: '#/components/schemas/Error'
 */
router.get("/:id", async (req, res) => {
    const id = z.number().parse(parseInt(req.params.id));

    const feedback = await db.feedback.findUnique({
        where: { id },
    });

    if (!feedback) {
        res.status(404).json({ error: "无对应反馈" });
        return;
    }

    res.json(feedback);
});


/**
 * @openapi
 * /feedback/list:
 *   post:
 *     summary: 获取反馈列表 <admin>
 *     description: "获取反馈列表，可以根据 entryId、userId 和 solved 进行筛选。<br /> 如果没有提供筛选条件，则返回所有反馈。<br />需要管理员权限。"
 *     tags: [Feedback]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               entryId:
 *                 type: integer
 *               userId:
 *                 type: integer
 *               solved:
 *                 type: boolean
 *     responses:
 *       200:
 *         description: 获取成功
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/Feedback'
 */
router.post("/list", async (req, res) => {
    const { entryId, userId, solved } = z
        .object({
            entryId: z.number().optional(),
            userId: z.number().optional(),
            solved: z.boolean().optional(),
        })
        .parse(req.body);

    // 无筛选条件，获取所有反馈
    if (!entryId && !userId && !solved) {
        const feedbacks = await db.feedback.findMany({
            orderBy: { id: "desc" },
        });
        res.json(feedbacks);
        return;
    }

    // 有筛选条件，根据 entryId、userId 和 solved 筛选反馈
    // 注意 entryId、userId、solved 可能都存在，也可能只有一个存在
    const feedbacks = await db.feedback.findMany({
        where: {
            entryId,
            userId,
            solved,
        },
        orderBy: { id: "desc" },
    });
    res.json(feedbacks);
    return;
});


/**
 * @openapi
 * /feedback/update:
 *   post:
 *     summary: 更新反馈 <admin>
 *     description: 添加/更新 response，或设置 solved 状态。response 和 solved 应至少指定其一。<br />需要管理员权限。
 *     tags: [Feedback]
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
 *               response:
 *                 type: string
 *               solved:
 *                 type: boolean
 *     responses:
 *       200:
 *         description: 更新成功
 *         content:
 *           $ref: '#/components/schemas/Feedback'
 *       404:
 *         description: 无对应反馈
 *         content:
 *           $ref: '#/components/schemas/Error'
 */
router.post("/update", async (req, res) => {
    // 管理员接口，用于添加或更新 response，或设置 solved 状态
    const { id, response, solved } = z
        .object({
            id: z.number(),
            response: z.string().optional(),
            solved: z.boolean().optional(),
        })
        .parse(req.body);

    if (!response && solved === undefined) {
        res.status(400).json({
            error: "response 和 solved 至少指定其一",
        });
    }

    try {
        const feedback = await db.feedback.update({
            where: { id },
            data: {
                response,
                solved,
            },
        });
        res.json(feedback);
    } catch (err) {
        res.status(404).json({ error: "无对应反馈" });
    }
});


/**
 * @openapi
 * /feedback/delete/{id}:
 *   get:
 *     summary: 删除反馈 <admin>
 *     description: 删除指定 ID 的反馈。需要管理员权限。<br />注：我认为实际业务中不会用到删除反馈的功能，这个接口应归类为一个开发、测试用接口。
 *     tags: [Feedback]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: 反馈 ID
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: 删除成功
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 message:
 *                   type: string
 *       404:
 *         description: 无对应反馈
 *         content:
 *           $ref: '#/components/schemas/Error'
 */
router.get("/delete/:id", async (req, res) => {
    const id = z.number().parse(parseInt(req.params.id));

    db.feedback
        .delete({
            where: { id },
        })
        .then(() => {
            res.json({ message: "成功删除反馈" });
        })
        .catch(() => {
            res.status(404).json({ error: "无对应反馈" });
        });
});

export default router;
