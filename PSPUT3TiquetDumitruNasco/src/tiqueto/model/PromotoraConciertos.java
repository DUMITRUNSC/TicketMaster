package tiqueto.model;

import tiqueto.EjemploTicketMaster;

public class PromotoraConciertos extends Thread {

	final WebCompraConciertos webCompra;

	public PromotoraConciertos(WebCompraConciertos webCompra) {
		super();
		this.webCompra = webCompra;
	}

	@Override
	public void run() {
		int entradasRepuestas = 0;


			while (entradasRepuestas < EjemploTicketMaster.TOTAL_ENTRADAS && !WebCompraConciertos.salida ) {

				synchronized (webCompra) {

					if (!WebCompraConciertos.todosFansCompletos) {

						if (!webCompra.hayEntradas()) {
							mensajePromotor("Reponiendo entradas............");
							int entradasRepuestasNuevas = webCompra.reponerEntradas(EjemploTicketMaster.REPOSICION_ENTRADAS);
							entradasRepuestas += entradasRepuestasNuevas;
							mensajePromotor("Se ha repuesto " + entradasRepuestasNuevas + " entradas. Total repuesto: " + entradasRepuestas);

						}
					}else{
						WebCompraConciertos.salida = true;
					}
				}
				try {
					Thread.sleep((int) (Math.random() * 5000) + 3000); // Dormir entre 3 y 8 segundos
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					return;
				}

			}

			synchronized (webCompra) {
				webCompra.cerrarVenta();
				webCompra.notifyAll(); // Notificar a todos en caso de que se haya terminado la venta
			}
		}


	/**
	 * Método a usar para cada impresión por pantalla
	 * @param mensaje Mensaje que se quiere lanzar por pantalla
	 */
	private void mensajePromotor(String mensaje) {
		System.out.println(System.currentTimeMillis() + "| Promotora: " + mensaje);

	}
}