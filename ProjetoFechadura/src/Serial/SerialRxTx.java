package Serial;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

import java.io.FileOutputStream;
import java.io.IOException;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;



public class SerialRxTx implements SerialPortEventListener { //implementa classe que trabalha para ouvir eventos da serial
	
	SerialPort serialPort = null;						// eventos que chegam pela porta serial
	
	private Protocolo protocolo = new Protocolo();
	private String appName;								//nome para aplicação
	
	private BufferedReader input;						//objeto para leitura na serial
	private OutputStream output;						// obejto para escrita na serial
	
	private static final int TIME_OUT = 1000; 			//tempo de espera da comunicação
	private static final int DATA_RATE = 9600;			//velocidade da comunicação
	
	private String serialPortName = "COM3";				//nome da porta serial que será conectada
	
	byte[] abrir = new byte[]{'a'};
	
	public boolean iniciaSerial() {
		boolean status =false;
		
		try {
			CommPortIdentifier portID = null;			// retorna as portas seriais disponíveis
			Enumeration portNum = CommPortIdentifier.getPortIdentifiers();			// atribui o nome das portas neste objeto
			
			while(portID == null && portNum.hasMoreElements()) {	//loop para verificar as portas seriais
				
				CommPortIdentifier listPortID= (CommPortIdentifier) portNum.nextElement();		// atribui a porta disponível
				
				if(listPortID.getName().equals(serialPortName) || listPortID.getName().startsWith(serialPortName)){		//verifica se a porta serial disponível é a que a nós queremos
					
					serialPort = (SerialPort) listPortID.open(appName, TIME_OUT); // atribui a porta serial a variável
					portID = listPortID;
					
					System.out.println("Conectado em : "+portID.getName());
					break; // finaliza o loop
					
				}
			}
			
			if(portID == null || serialPort == null) { 			//verificação caso aconteceu algum erro na escolha da porta serial
				return false;
			}
				
			serialPort.setSerialPortParams(DATA_RATE, serialPort.DATABITS_8, serialPort.STOPBITS_1, serialPort.PARITY_NONE); 	//termina de setar os parâmetros para a porta serial escolhida
				
			serialPort.addEventListener(this); 					//verifica eventos nesta classe
			serialPort.notifyOnDataAvailable(true);  			//notifica quando estiver tudo pronto para enviar dados
			status = true;
				
			try {
				Thread.sleep(1000);								// delay para começar a enviar e receber dados
					
			} catch (InterruptedException e) {					// caso aconteça algum erro na transmissão na serial
				e.printStackTrace();
				status = false;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
	
	//método que envia dados pela serial
	public void sendData(int data) {
		try {			
			output = serialPort.getOutputStream();		//retorna um fluxo para começar a enviar dados
			output.write(data);

		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	
	//método que fecha a conexão com a serial
	public synchronized void closeConnection() {
		if(serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
			System.out.println("Conexão fechada com sucesso");
		}
	}
	
	@Override
	public void serialEvent(SerialPortEvent eventSerial) {
		
		try {
			switch (eventSerial.getEventType()) {		//quando tiver dados na serial
				case SerialPortEvent.DATA_AVAILABLE:	//quando os dados estiverem disponíveis na porta serial
				
					if(input == null) {				//quando entrada estiver vazia
						input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));	 //entrada recebe um Buffer de leitura
												// este buffer recebe uma entrada de leitura que por sua vez recebe o get do arquivo que está na serial port manipulada
					}
					
					if(input.ready()) {		//verifica se o BufferReader está pronto para ser utilizado
						
						protocolo.setLeituraComando(input.readLine());	// imprime todas as linhas que chegarem pela serial
						System.out.println("Chegou: "+protocolo.getLeituraComando()); 	// imprime os dados que chegaram pela serial

					}
					
					break;
					
				default:
						break;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public Protocolo getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(Protocolo protocolo) {
		this.protocolo = protocolo;
	}

	
}
