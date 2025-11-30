-- CreateTable
CREATE TABLE "user_profiles" (
    "id" UUID NOT NULL DEFAULT gen_random_uuid(),
    "user_id" UUID NOT NULL,
    "bio" VARCHAR(150),

    CONSTRAINT "user_profiles_pkey" PRIMARY KEY ("id")
);

-- AddForeignKey
ALTER TABLE "user_profiles" ADD CONSTRAINT "fk_ubs_user" FOREIGN KEY ("user_id") REFERENCES "users"("user_id") ON DELETE CASCADE ON UPDATE NO ACTION;
