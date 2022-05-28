module launchofexile {
    requires java.desktop;
    requires java.net.http;
    requires com.formdev.flatlaf;
    requires com.fasterxml.jackson.databind;
    requires jdk.crypto.cryptoki;
    opens com.github.cptblacksheep.launchofexile.datamanagement;
}