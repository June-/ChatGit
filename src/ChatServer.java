/*
 *  Q1. Server端读消息的方式是阻塞的，导致程序无法接受后续Client端的连接
 *  A1. 把接受Client端连接和收发消息分开来写，每收到一个Client端连接，就为之起一个线程处理消息。
 *
 *  Q2. main()方法为static, 怎样在其中使用非static的内部类Client？
 *  A2. 引入函数start(), 在start()中定义类Client的对象，再在main()中调用start()。
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    
        private boolean started = false;     // 表示server是否已绑定到端口
        private ServerSocket ss = null;
        List<Client> clients = new ArrayList<Client>();  // 保存连接到Server的所有Client
        String allClientsName = "";
        //String curClientName = "";

        public void start() {
            // Server端绑定端口
            try {
                ss = new ServerSocket(8888);  
                started = true;
            } catch (BindException e) {
                System.out.println("端口使用中...");
                System.out.println("请解除占用并重新运行server端");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Server端处理Client端请求
            try {
                // 当server端已启动：即可接受client端连接
                while (started) {
                    Socket s = ss.accept();
                    Client c = new Client(s);
                    new Thread(c).start();
                    clients.add(c);
System.out.println("\nLn42 start(): a client connected!");
                
                    //dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    private class Client implements Runnable {
        private Socket s;
        private DataInputStream  dis = null;  // 负责从socket读消息内容
        private DataOutputStream dos = null;  // 负责把消息转发至Client
        private boolean bConnected = false;
        private String clientName = "";

        // 初始化一个Client端的连接
        public Client(Socket s) {
            this.s = s;
            try {
                dis = new DataInputStream(s.getInputStream());
                dos = new DataOutputStream(s.getOutputStream());
                bConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Server向这个Client发送消息
        public void send(String str) {
            try {
                dos.writeUTF(str);
            } catch (IOException e) {
                clients.remove(this);
System.out.println("Ln83 send(): Client \"" + clientName + "\" quited, server removed it from group.");
            }
        }
        
        public void broadcast(String str) {
        	for (int i = 0; i < clients.size(); i++) {
        		Client c = clients.get(i);
        		c.send(str);
        	}
        }

        public void run() {
            try {
                //当Client端已连接：即可反复接收client端消息
                while (bConnected) {
                    String str = dis.readUTF();
System.out.println("Ln92 run(): " + str + " recved.");

                    String[] sArray = str.split("-");
                    
					// 用户登录
                    if (sArray[0].equals("usr")) {
    					clientName = sArray[1];	//	当前发送消息的用户名字
                        allClientsName += clientName + "-";
                        str = "usr-" + allClientsName;
//System.out.printf("Ln97: clientName: %s, allClientsName: %s\n", clientName, allClientsName);
                    } 
                    // 用户退出
                    else if (sArray[0].equals("qui")) {
                    	clientName = sArray[1];                    	
                    	allClientsName = allClientsName.replace(clientName + "-", "");
                    	str = "usr-" + allClientsName;
                    	bConnected = false;
                    } 
                    // 消息内容 clientName:msgText
                    else
                    	str = clientName + str;
                    
                    // 消息转发
                    if (!allClientsName.equals("")) { 
                    	broadcast(str);
System.out.printf("Ln127 run(): " + "\"%s\" broadcast\n", str) ;
                    }
                }
            } catch (EOFException e) {
            	allClientsName = allClientsName.replace(clientName + "-", "");
            	broadcast("usr-" + allClientsName);
System.out.println("Ln129 run(): Client \"" + clientName + "\" closed!");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // 遇到任何异常，都应立即关闭dis管道和套接字s
                try {
                    if (dis != null) 
                    { dis.close(); }
                    if (dos != null) 
                    { dos.close(); }
                    if (s != null) 
                    { s.close();  s = null; }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    
    public static void main(String[] args) {
        new ChatServer().start();
    }
}

