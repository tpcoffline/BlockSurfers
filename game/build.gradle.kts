plugins {
    application
}

dependencies {
    implementation(project(":common"))
}

application {
    mainClass.set("me.tpcoffline.blocksurfers.game.GameServer")
}