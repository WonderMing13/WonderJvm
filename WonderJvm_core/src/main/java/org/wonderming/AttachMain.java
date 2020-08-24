package org.wonderming;

import com.sun.tools.attach.VirtualMachine;
import org.wonderming.server.NettyCoreServer;


/**
 * @author wangdeming
 **/
public class AttachMain {

    public static void main(String[] args) throws Exception {
        final VirtualMachine vm = VirtualMachine.attach("10263");
        try{
            vm.loadAgent("/Users/wangdeming/WonderJvm/WonderJvm_agent/target/WonderJvm_agent-1.0-SNAPSHOT-jar-with-dependencies.jar","namespace=namespace&server.ip=127.0.0.1&server.port=8010");
        }finally {
            vm.detach();
        }
    }
}
