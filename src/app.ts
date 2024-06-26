import express, { Application, ErrorRequestHandler } from "express";
import "express-async-errors";
import cors from "cors";
import bodyParser from "body-parser";
import { ZodError } from "Zod";

import UserRouter from "./user";
import EntryRouter from "./entry";
import AdminRouter from "./admin";

const app: Application = express();

// 配置 swagger-ui

const swaggerJsdoc = require("swagger-jsdoc");
const swaggerUi = require("swagger-ui-express");
const swaggerOptions = require("../swagger-options.json");
const openapiSpec = swaggerJsdoc(swaggerOptions)
app.use("/docs", swaggerUi.serve, swaggerUi.setup(openapiSpec))

// 配置跨域
app.use(cors())

// 配置静态资源
app.use("/avatars", express.static("./static/avatars"));
app.use("/images", express.static("./static/images"));

// 配置中间件
app.use(bodyParser.json());

// 配置路由
app.use("/user", UserRouter);
app.use("/entry", EntryRouter);
app.use("/admin", AdminRouter);

// 配置根路由（仅用于确认服务是否可用）
app.get("/", (req, res) => {
    res.send("<h1>后端服务可用</h1><a href='/docs'>查看接口文档：<code>/docs</code></a>");
});

// 配置错误处理中间件
const errorHandler: ErrorRequestHandler = (err, req, res, next) => {
    if (err.name === "UnauthorizedError") {
        res.status(401).json({
            error: "未授权的访问：无鉴权信息或 token 无效",
        });
        return;
    }

    if (err instanceof ZodError) {
      res.status(400).json({
        error: "参数类型错误"
      })
    }

    console.error(err);
    res.status(500).json({
        error: "服务器内部错误",
    });
};
app.use(errorHandler);

export default app;