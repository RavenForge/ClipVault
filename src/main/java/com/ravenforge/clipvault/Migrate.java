package com.ravenforge.clipvault;

import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;

import java.io.IOException;

public class Migrate {

    static void main() throws IOException {
        DbMigration dbMigration = DbMigration.create();
        dbMigration.setPlatform(Platform.SQLITE);

        dbMigration.generateMigration();
    }
}
