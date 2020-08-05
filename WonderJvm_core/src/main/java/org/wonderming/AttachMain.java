package org.wonderming;

import com.sun.tools.attach.VirtualMachine;
import org.wonderming.server.NettyCoreServer;


/**
 * @author wangdeming
 **/
public class AttachMain {

    public static void main(String[] args) throws Exception {
        System.out.println("Test Attach....");
        final VirtualMachine vm = VirtualMachine.attach("7744");
        try{
            vm.loadAgent("/Users/wangdeming/WonderJvm/WonderJvm_agent/target/WonderJvm_agent-1.0-SNAPSHOT-jar-with-dependencies.jar");
        }finally {
            vm.detach();
        }
    }
}
