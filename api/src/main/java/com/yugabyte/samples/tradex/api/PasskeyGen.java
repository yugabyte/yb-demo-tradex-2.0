package com.yugabyte.samples.tradex.api;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasskeyGen {
    public static void main(String[] args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("mickey@tradex.com123"));
        System.out.println(bCryptPasswordEncoder.encode("donald123"));
        System.out.println(bCryptPasswordEncoder.encode("sally123"));
        System.out.println(bCryptPasswordEncoder.encode("molly123"));
        System.out.println(bCryptPasswordEncoder.encode("leo123"));
        System.out.println(bCryptPasswordEncoder.encode("scrooge123"));


    }
    //mickey$2a$10$0g2UGK0Gv.CF0iNw0m0vIe41evmbvj8d1DcM7lCcaAQmXD9BPxIUS
    //mickey$2a$10$gsybhSISsJ0CRGJd7EX9qOt1nfDdrDloHMmypn1vlEIhHYqGFPnzm
}

/*
$2a$10$.F2QPGfG8YzHRqQ1o5uuLeHiWPxLwinmFz67TIEg.4VS8PHITiHxy
$2a$10$wK4JTnG6H02BkTBpyqbfi.O1YyMC.81FM1biSEtrvqRbA005/mR.m

$2a$10$7XeV3D6WojF7rqWleC6U5ugX17wLobgYZdlqGfAtDXvya3R4bWguS
$2a$10$eKA4weYPKxwbfjsPE.VwG.qH1ymBKL61KwaQT5xp9QYPR25xzDL26

 */
