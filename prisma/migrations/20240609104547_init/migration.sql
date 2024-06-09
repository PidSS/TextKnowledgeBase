/*
  Warnings:

  - Made the column `introduction` on table `Entry` required. This step will fail if there are existing NULL values in that column.

*/
-- RedefineTables
PRAGMA defer_foreign_keys=ON;
PRAGMA foreign_keys=OFF;
CREATE TABLE "new_Entry" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" TEXT NOT NULL,
    "introduction" TEXT NOT NULL,
    "content" TEXT NOT NULL
);
INSERT INTO "new_Entry" ("content", "id", "introduction", "name") SELECT "content", "id", "introduction", "name" FROM "Entry";
DROP TABLE "Entry";
ALTER TABLE "new_Entry" RENAME TO "Entry";
CREATE UNIQUE INDEX "Entry_name_key" ON "Entry"("name");
PRAGMA foreign_keys=ON;
PRAGMA defer_foreign_keys=OFF;
