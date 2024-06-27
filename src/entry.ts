/**
 * @openapi
 * tags:
 *   name: Entry
 *   description: 知识相关接口
 * components:
 *   schemas:
 *     Error:
 *       application/json:
 *         schema:
 *           type: object
 *           properties:
 *             error:
 *               type: string
 *     EntryInfo:
 *       application/json:
 *         schema:
 *           type: object
 *           properties:
 *             id:
 *               type: integer
 *             name:
 *               type: string
 *             introduction:
 *               type: string
 *             content:
 *               type: string
 */

import express, { Router } from "express";
import { Prisma, PrismaClient } from "@prisma/client";
import { z } from "Zod";

const router: Router = express.Router();

const db = new PrismaClient();

/**
 * @openapi
 * /entry/read/{id}:
 *   get:
 *     summary: 获取指定知识条目的完整信息
 *     tags: [Entry]
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: 知识条目的 ID
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: 成功获取知识条目
 *         content:
 *           $ref: '#/components/schemas/EntryInfo'
 *       404:
 *         description: 未找到对应条目
 *         content:
 *           $ref: '#/components/schemas/Error'
 */
router.get("/read/:id", (req, res) => {
    const id = z.number().parse(parseInt(req.params.id));

    // prettier-ignore
    db.entry.findUnique({ where: { id } })
        .then( data => {
            if (data) res.json(data);
            else
                res.status(404).json({
                    error: "未找到对应条目",
                });
        })
        .catch((error) => {
            throw Error;
        });
});


/**
 * @openapi
 * /entry/list:
 *   get:
 *     summary: 获取所有知识条目的完整信息
 *     description: 返回所有知识条目的完整信息。这个接口主要用于开发测试，之后会再提供一个只返回条目简要信息的接口，专门用于获取首页所需的数据。
 *     tags: [Entry]
 *     responses:
 *       200:
 *         description: 成功获取全部知识条目
 *         content:
 *           application/json:
 *             schema:
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
 *                   content:
 *                     type: string
 */
router.get("/list", (req, res) => {
    db.entry.findMany().then((data) => {
        res.json(data);
    });
});


router.get("/recommend");


/**
 * @openapi
 * /entry/search:
 *   post:
 *     summary: 搜索知识条目
 *     description: 根据关键词搜索知识条目，返回 ID、名称和简介。关键词之间可以用空格分隔，搜索结果为包含任意关键词的条目。
 *     tags: [Entry]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               query:
 *                 type: string
 *     responses:
 *       200:
 *         description: 搜索成功（无对应数据时返回空数组）
 *         content:
 *           application/json:
 *             schema:
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
 *       400:
 *         description: 参数类型错误
 *         content:
 *           $ref: '#/components/schemas/Error'
 */
router.post("/search", (req, res) => {
    const query = z.string().parse(req.body.query as string);
    const koywords = query.split(" ")
    db.entry
        .findMany({
            where: {
                OR: koywords.map((k) => ({
                    name: {
                        contains: k,
                    },
                })),
            },
            select: {
                id: true,
                name: true,
                introduction: true
            }
        })
        .then((data) => {
            res.json(data);
        });
});

export default router;
