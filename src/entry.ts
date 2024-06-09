import express, { Router } from "express";
import { Prisma, PrismaClient } from "@prisma/client";
import { z } from "Zod";

const router: Router = express.Router();

const db = new PrismaClient();

router.get("/read/:id", (req, res) => {
    const id = z.number().parse(parseInt(req.params.id));

    // prettier-ignore
    db.entry.findUnique({ where: { id } })
        .then((data) => {
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

router.get("/list");

router.get("/recommend");

router.get("/search");

export default router;
