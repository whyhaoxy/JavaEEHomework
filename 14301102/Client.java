import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	public static void main(String args[]){
		try {
			System.out.println("客户端正在发送字符串：Hello World!");
			doGet("127.0.0.1", 3333, "Hello World!");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void doGet(String host, int port, String info) throws UnknownHostException, IOException, InterruptedException{
		Socket socket = null;
		
		socket = new Socket(host, port);//连接服务器
		
		StringBuffer sb = new StringBuffer(info);
		
		OutputStream socketOut = socket.getOutputStream();
		socketOut.write(sb.toString().getBytes());//发送信息
		
		Thread.sleep(2000);
		
		InputStream socketIn = socket.getInputStream();
		int size = socketIn.available();
		byte[] buffer = new byte[size];
		socketIn.read(buffer);//接受返回的字符串
		
		System.out.println("接收服务器发回的字符串：" + new String(buffer));
		
		socket.close();
	}
}
