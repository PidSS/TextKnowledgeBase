import { JwtUser } from "./types";

declare global {
    namespace Express {
        interface Request {
            auth?: JwtUser;
        }
    }
}