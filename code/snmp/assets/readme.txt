security check:

1. com.mrd.bitlib, com.snmp.wallet.security

2. check word list security
   Bip39.MasterSeed, private wordlist source, check method  
   only from GenerateMain, BtcSecurityStorage
   
3. check private key security
   only call from BtcSecurityStorage.getPrivateKey()
   only sendtx need private key
   
4. check word list crypto storage: SecurityBacking, no words output

5. check network action, search: java.net  okhttp3 org.apache android.net

6. sign

    <!-- mycelium lib base version tag=v3.15.0.0 --> for base lib from github
    <!-- https://repo1.maven.org/maven2/com --> for lib jar
    <!-- bcprov-jdk15to18-171.jar -->
    <!-- zxing-android-embedded v4.3.0 -->
    <!-- zip use utf-8 param: cu=on --> for zip
    
bitlong
nsec17q75t5ny4smfgphhf6f07l7ssk5rm30ker943s4m8amm6d8hfg5sn7nkhy
npub1dd0mxchj4mxu9e20s7gs02mmfld98lrkyfu8a9tmx4nzx96u77hq5gcrzq
