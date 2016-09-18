import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String args[]){
		final int port = 3333;
		ServerSocket serverSocket = null;
		
		
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("服务器正在监视端口： " + serverSocket.getLocalPort());
			while(true){
				Socket clientSocket = serverSocket.accept();
				System.out.println("建立了与客户的一个新的TCP连接，该客户的地址为：" + clientSocket.getInetAddress() + "：" + clientSocket.getPort());
				
				serverThread thread = new serverThread(clientSocket);
				Thread line = new Thread(thread);
				
				line.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

class serverThread implements Runnable{
	Socket clientSocket = null;
			
	public serverThread(Socket clientSocket){
		this.clientSocket = clientSocket;
	}
	
	public void run(){
		try {
			InputStream socketIn = clientSocket.getInputStream();
			Thread.sleep(500);
			
			int size = socketIn.available();
			byte[] buffer = new byte[size];
			socketIn.read(buffer);
			String request = new String(buffer);
			System.out.println("请求反转字符串：" + request);
			
			String result = reverse(request);
			System.out.println("反转成功：" + result);
			
			OutputStream socketOut = clientSocket.getOutputStream();
			socketOut.write(result.getBytes());
			Thread.sleep(1000);
			
			clientSocket.close();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 反转字符串
	 * @param request 客户端发来的字符串
	 * @return	 反转的字符串
	 */
	public String reverse(String request){
		char[] str = request.toCharArray();
		
		int begin = 0;
		int end = str.length - 1;
		
		while(begin < end){
			char a = str[begin];
			str[begin] = str[end];
			str[end] = a;
			begin++;
			end--;
		}
		
		return new String(str);
	}
}