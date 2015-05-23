import java.awt.*;
import java.awt.event.*;
import java.io.*;
//import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;


public class Login extends Dialog {
	

	
	private static final long serialVersionUID = 1L;
	boolean succeed = false;
    String usrnameTxt, pswdTxt;
    TextField usrname = new TextField(11);
    TextField  pswd = new TextField(15);
    Button btSignin = new Button("登录");
    Button btCancel = new Button("取消");
    Button btSignup = new Button("注册");
    Panel panel1 = new Panel();
    Panel panel11 = new Panel();
    Panel panel12 = new Panel();
    Panel panel13 = new Panel();
    ArrayList<Account> arrList = new ArrayList<Account>();
    
 
   //public class Account implements Serializable{
   //    public String usrname_;
   //    public String pswd_;
   //}
    
    public Login(Frame owner, boolean modal) {
		super(owner, modal);
	}

    public void launchFrame() {
        setTitle("Login");
        setLocation(400, 300);
        setSize(500, 300);

        panel1.setLayout(new BorderLayout());
        panel11.add(new Label("用户ID："));
        panel11.add(usrname);
        panel12.add(new Label("密  码："));
        panel12.add(pswd);
        panel13.add(btSignin);
        panel13.add(btCancel);
        panel13.add(btSignup);
        panel1.add(panel11, BorderLayout.NORTH);
        panel1.add(panel12, BorderLayout.CENTER);
        panel1.add(panel13, BorderLayout.SOUTH);

        add(panel1);


        // 登录控制
        btSignin.addActionListener(new ActionListener() {
            private ObjectInputStream is = null;

            public void actionPerformed(ActionEvent e) {

                try {
                    is = new ObjectInputStream(
                        new FileInputStream("account"));
                    ArrayList<Account> list = (ArrayList<Account>) is.readObject();
                    for (Iterator<Account> it =  list.iterator(); it.hasNext(); ) {
                        Account temp = (Account)it.next();
                        usrnameTxt = usrname.getText();
                        pswdTxt = pswd.getText();
                        if (temp.getUsrname().equals(usrnameTxt) &&
                            temp.getPswd().equals(pswdTxt)) {
                        	
                            temp.setOnline(true);
                            succeed = true;
//System.out.println(temp.getUsrname() + " isOnline: " + temp.isOnline());
//System.out.println("login.succeed: " + succeed);
                            
                        	//ChatClient cc = new ChatClient();
                            //cc.launchFrame();
                        	
                            setVisible(false);
                            break;
                        }
                    }                    
                    is.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				} 
            }
        });

        // 注册
        btSignup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    // Java序列化把对象存储到文件中
                    Account account = new Account();
                    account.setUsrname(usrname.getText());
                    account.setPswd(pswd.getText());
                    arrList.add(account);
                    try {
						ObjectOutputStream os = new ObjectOutputStream(
						    new FileOutputStream("account"));
						os.writeObject(arrList);
						os.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

            }
        });

        btCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                usrname.setText("");
                pswd.setText("");
            }
        });

        this.addWindowListener(new WindowAdapter() {
            // 窗口关闭事件
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        
        setVisible(true);
    }

    public boolean succeed() 
    { return succeed; }

    public String getUsrname() 
    { return usrnameTxt; }

    public static void main(String[] args) {
        //new Login().launchFrame();
    }
}
