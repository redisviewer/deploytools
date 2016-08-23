package com.thinkjoy;

import com.thinkjoy.service.DeployAbstract;
import com.thinkjoy.service.DeployImplement;

public class Main {

    public static void main(String[] args) {
        DeployAbstract executor = new DeployImplement();
        executor.execute();
    }
}
