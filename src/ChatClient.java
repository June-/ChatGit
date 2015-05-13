import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends Frame {
    private Socket s = null;   //套接字 
    private DataOutputStream dos = null;  // 负责向Server端发送消息
    private DataInputStream  dis = null;  // 负责读取Server端转发的其他Client的消息
    private boolean bConnected = false;   //
 

    Thread tRecv = new Thread(new RecvThread());


    TextField tfTxt = new TextField();      //消息输入区
    TextArea taContent = new TextArea();    //消息显示区
    TextArea taFriends = new TextArea();
    List lst = new List(4, false);
    Panel panel = new Panel();

    // 产生一个窗口
    public void launchFrame() {
        setTitle("Chat Client");
        setLocation(400, 300);
        setSize(600, 600);
        
        lst.add("user1");
        lst.add("user2");
        lst.add("user3");
        //panel.setLayout(new BorderLayout());
        panel.add(taContent, BorderLayout.NORTH);
        panel.add(tfTxt, BorderLayout.SOUTH);
        add(panel, BorderLayout.WEST);
        add(lst, BorderLayout.EAST);
        pack();

        this.addWindowListener(new WindowAdapter() {
            // 窗口关闭事件
            public void windowClosing(WindowEvent arg0) {
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
            dos.close();
            dis.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
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
            try {
                dos.writeUTF(str);
                dos.flush();
                //dos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    // 读取和显示Server端转发的其他Client的消息
    private class RecvThread implements Runnable {
        
        public void run() {
            try {
                while (bConnected) {
                    String str = dis.readUTF();  // 读取消息
                    taContent.setText(taContent.getText() + str + '\n');  // 显示消息
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

    public static void main(String[] args) {
        new ChatClient().launchFrame();
    }

}
