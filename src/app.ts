import express, { Application, ErrorRequestHandler } from "express";
import bodyParser from "body-parser";
import { ZodError } from "Zod";
import UserRouter from "./user";
import EntryRouter from "./entry";

const app: Application = express();

app.use("/avatars", express.static("../static/avatars"));
app.use("/images", express.static("../static/images"));

app.use(bodyParser.json());

app.use("/user", UserRouter);
app.use("/entry", EntryRouter);

app.get("/", (req, res) => {
    res.send("后端服务可用");
});

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

    res.status(500).json({
        error: "服务器内部错误",
    });
};
app.use(errorHandler);

export default app;
