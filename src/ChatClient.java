import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

public class ChatClient extends Frame {
	private String Usrname = "";
    private Socket s = null;   //套接字 
    private DataOutputStream dos = null;  // 负责向Server端发送消息
    private DataInputStream  dis = null;  // 负责读取Server端转发的其他Client的消息
    private boolean bConnected = false;   //
    private ObjectInputStream is = null;  // 用于从文件读取用户列表 

    Thread tRecv = new Thread(new RecvThread());

    TextField tfTxt = new TextField();      //消息输入区
    TextArea taContent = new TextArea();    //消息显示区
    TextArea taFriends = new TextArea();
    List friendList = new List(4, false);
    Panel panel = new Panel();

    // 产生一个窗口
    public void launchFrame() {
        setTitle("Group Chat");
        setLocation(400, 300);
        setSize(600, 600);
        
        panel.setLayout(new BorderLayout());
        panel.add(taContent, BorderLayout.NORTH);
        panel.add(tfTxt, BorderLayout.SOUTH);
        add(panel, BorderLayout.WEST);
        add(friendList, BorderLayout.EAST);
        pack();

        this.addWindowListener(new WindowAdapter() {
            // 窗口关闭事件
        	public void windowClosing(WindowEvent arg0) {
        		send("qui-" + Usrname);
        		disconnect();
                System.exit(0);
        	}
        });

        tfTxt.addActionListener(new TFListener());  // TextField事件处理

        setVisible(true);	// 显示client端窗口
        connect();  // client连上服务器
    
        tRecv.start();
    }

    // client连接服务器
    public void connect() {
        try {
            s = new Socket("127.0.0.1", 8888);
            dos = new DataOutputStream(s.getOutputStream());
            dis = new DataInputStream(s.getInputStream());
System.out.println("connected!");
            bConnected = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
        	bConnected = false;
            dos.close();
            dis.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    public void send(String str) {
        try {
            dos.writeUTF(str);
            dos.flush();
            //dos.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    // TextField事件处理
    private class TFListener implements ActionListener {

    	    public void actionPerformed(ActionEvent e) {
    	    // 把输入的消息显示出来
            String str = tfTxt.getText().trim();
            //taContent.setText(str);
            tfTxt.setText("");

            // client把消息发送给server（可多行）
            send(str);
        }
    }

    // 读取和显示Server端转发的其他Client的消息
    private class RecvThread implements Runnable {
        
        public void run() {
            try {
                while (bConnected) {
                    String str = dis.readUTF();  // 读取消息
                    String[] sArray = str.split("-");
                    // friendList有变化，更新之
                    if (sArray[0].equals("usr")) {
                    	friendList.clear();
                    	for (int i = 1; i < sArray.length; i++) {
                    		if (!sArray[i].equals("")) 
                    			friendList.add(sArray[i]);
                    	}
                    } 
                    // 收到消息，显示
                    else {
                    	taContent.setText(taContent.getText() + str + '\n');  // 显示消息
                    }
                }
            } catch (SocketException e) {
System.out.println("quit, bye! (SocketException)");
            } catch (EOFException e) {
System.out.println("quit, bye! (EOFException)");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUsrname(String arg)
    { Usrname = arg; }
    
    public String getUsrname()
    { return Usrname; } 
    
    public static void main(String[] args) {
    	ChatClient cc = new ChatClient();
    	Login login = new Login(cc, true);
		login.launchFrame();
//System.out.println("login.succeed(): " + login.succeed());

    	if (login.succeed()) {
System.out.println("login.succeed(): " + login.succeed());
            cc.launchFrame();
            cc.setUsrname(login.getUsrname());
			cc.send("usr-" + login.getUsrname());	// Client登陆后发送用户名给服务器
    	} 
    	else {
    		//messageBox("Login fail");
    		System.out.println("(else) login fail");
    	}
    }

}
