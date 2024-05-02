import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientConsole {
	public String SERVER_ADDRESS = "10.0.0.174";
	public int SERVER_PORT = 7777;
	public static ObjectOutputStream objectOutputStream;
	public static ObjectInputStream objectInputStream;
	public OutputStream outputStream;
	public InputStream inputStream;
	boolean signedIn = false;
	Scanner scan;
	String currentUser;
	
	protected void startgamePlay() throws IOException {
		scan = new Scanner(System.in);
		Socket socket = createConnection();
		initializeStreams(socket);
		System.out.println("\n\nWelcome to BlackJack Console Ui:\n 1.SignIn \n 2.Signup\n\n");
		int choice = scan.nextInt();
		switch(choice) {
		case 1:
			signIn();
			signedIn = true;
			break;
		case 2:
			signUp();
			signedIn = true;
			break;
		}
		while (true) {
			System.out.println("Welcome to BlackJack v0.1 release\nMain Menu\n1. OPEN GAME \n2. List-Games\n3. Join-Game\n4. List Player In Game"
					+ "\n5.Add Funds\n6.Option\n7.Option\n8.option\n9.option\n0.Logout");
			choice = scan.nextInt();
			switch(choice) {
			case 1:
				System.out.println("Opening a Game");
				openGame();
				break;
			case 2:
				System.out.println("Listing the Games:");
				listGame();
				break;
			case 3:
				System.out.println("Joining the Game");
				joinGame();
				break;
			case 4: 
				System.out.println("Listing All Players in the Game");
				listPlayersInGame();
				break;
			case 5:
				System.out.println("Adding Funds to the game");
				addFunds();
				break;
			case 6:
				System.out.println("Closing The Game");
				closeGame();
				break;
			case 7:
				System.out.println("Leaving the Game");
				leaveGame();
				break;
			case 8:
				System.out.println("List Players Online");
				listPlayerOnline();
				break;
				
			case 9:
				System.out.println("List Dealer Online");
				listDealersOnline();
				break;
				
			case 0:
				logOut();
				signedIn = false;
			
			}
		}
		
	}

	private void listDealersOnline() {
		// TODO Auto-generated method stub
		
	}

	private void listPlayerOnline() {
		// TODO Auto-generated method stub
		
	}

	private void leaveGame() {
		// TODO Auto-generated method stub
		
	}

	private void closeGame() {
		// TODO Auto-generated method stub
		
	}

	private void addFunds() {
		System.out.println("Adding funds to your account, How Much ? " );
		String amount = scan.next();
		String requestUserNameandFund = currentUser+":"+amount;
		sendMessage(new Message(Type.AddFunds, Status.New, requestUserNameandFund));
		String response = receiveMessage().getStatus() +" "+receiveMessage().getType()+" "+receiveMessage().getText();
		System.out.println(response);
	}

	private void logOut() {
		sendMessage(new Message(Type.Logout, Status.New, ""));
		String response = receiveMessage().getStatus() +" "+receiveMessage().getType()+" "+receiveMessage().getText();
		System.out.println(response);
		
	}

	private void listPlayersInGame() {
		System.out.println("Enter a Game Number");
		String gameNumber = scan.next();
		sendMessage(new Message(Type.ListPlayersInGame,Status.New,gameNumber));
		String response = receiveMessage().getStatus() +" "+receiveMessage().getType()+" "+receiveMessage().getText();
		System.out.println(response);
		
	}

	private void joinGame() {
		System.out.println("Joining Game");
		sendMessage(new Message(Type.JoinGame, Status.New, "1"));
		String response = receiveMessage().getText().toString();
		System.out.println(response);
	}

	private void listGame() {
		String response ="";
		System.out.println("List Of All Games");
		sendMessage(new Message(Type.ListGames,Status.New,""));
		System.out.println(receiveMessage().getStatus().toString()+" "+receiveMessage().getText().toString());
		response = receiveMessage().getText().toString();
		System.out.println(response);
		
	}

	private void openGame() {
		String response = "";
		System.out.println("Opening New table");
		sendMessage(new Message(Type.OpenGame, Status.New, ""));
		response = receiveMessage().getStatus().toString();
		System.out.println(response);
	}

	private void signIn() {
		String response = "";
		String credentials = "";
		System.out.println("Enter your Username: ");
		String userName = scan.next();
		credentials = credentials + userName;
		System.out.println("Enter your Password: ");
		credentials = credentials+":"+scan.next();
		System.out.println("Credentials sent to Server = "+ userName);
		sendMessage(new Message(Type.Login,Status.New,credentials));
		response = receiveMessage().getStatus().toString();
		System.out.println(response);
		//System.out.println("Message Received From Server- "+receiveMessage().getType()+receiveMessage().getStatus());
		if (response == "Success") {
			// Go to Next Screen
			System.out.println("Success, Going to Another Screen\n");
			currentUser = userName;
		}
		else {
			System.out.println("Invalid Credentail Provided, Try Again");
		}
	}

	private void signUp() {
		Message response = null;
		String signUpCredentials = "";
		System.out.println("USER/DEALER SIGNUP PAGE\nEnter your Username: ");
		String userName = scan.next();
		signUpCredentials = signUpCredentials + userName;
		System.out.println("Enter your Password: ");
		signUpCredentials = signUpCredentials+":"+scan.next();
		System.out.println("Credentials sent to Server = "+ userName);
		sendMessage(new Message(Type.Login,Status.New,"luser1:letmein")); // Someone Needs to be signed in in order to create a User
		sendMessage(new Message(Type.Register,Status.New,signUpCredentials));
		response = receiveMessage();
		System.out.println(response.getStatus());
		if (response.getStatus().toString().equals("Success")) {
			System.out.println("SignUp Successful, You are also now logged in\n");
			sendMessage(new Message(Type.Login,Status.New,signUpCredentials));
			currentUser = userName;
		}
		else {
			System.out.println("Something Went Wrong, Please try again Later");
		}
	}

	Socket createConnection() {

		Socket socket = null;
		try {
			socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Connected to server.");

		return socket;
	}

	void initializeStreams(Socket socket) throws IOException {
		// Initialize streams
		outputStream = socket.getOutputStream();
		inputStream = socket.getInputStream();
		objectOutputStream = new ObjectOutputStream(outputStream);
		objectInputStream = new ObjectInputStream(inputStream);
		System.out.println("Output Input Streams Initialized");
	}

	public void sendMessage(Message message) {
		try {
			objectOutputStream.writeObject(message);
			objectOutputStream.flush();
		} catch (IOException e) {
			System.err.println("Error sending message: " + e.getMessage());
		}
	}

	public Message receiveMessage() {
		try {
			return (Message) objectInputStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Error receiving message: " + e.getMessage());
			return null;
		}
	}

}