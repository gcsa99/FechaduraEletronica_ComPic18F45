package Fechadura;
import Serial.SerialRxTx;
import java.util.Scanner;

public class Fechadura {

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		SerialRxTx serial = new SerialRxTx();
		Senha password = null;
		String newKey;
		int aux = 0;
		int contador = 0;
		
		if(serial.iniciaSerial()) {
			password = new Senha();
			
			while(true) {
				
				System.out.println("Digite sua senha de 8 caracteres: ");
				
				while(true) {
					newKey = in.nextLine();
					if(newKey.length() <= password.getMax()){
						
						if(newKey.equals(password.getAdmin())) {
							serial.sendData((int) 'b');
							System.out.println("Você entrou no modo MUDAR SENHA");
							password.newPassword(newKey);
							serial.sendData((int) 'c');

							break;
							
						}
						else {
							serial.sendData((int) 'd');			//não foi possível mudar a senha;
							if(newKey.equals(password.getPassword())){
								serial.sendData((int) 'a');
								System.out.println("Porta aberta");
								break;
							}
							
							else {
								System.out.println("Senha incorreta\n");
								password.setContador(++contador);
								
								if(password.getContador() >=3) {
									serial.sendData((int) 'e');
									password.alarme();
									contador =0;
									serial.sendData((int) 'f');
								}
								//if(password.getContador() < 3) {
									
					//			}
					
								break;
							}
									
						}
				}
					else {
						System.out.println("\nDigite apenas 8 caracteres");
						break;
					}
				}
			}
		}
		else {
			
		}
	}

}
