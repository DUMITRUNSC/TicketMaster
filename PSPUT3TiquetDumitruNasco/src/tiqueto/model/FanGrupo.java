package tiqueto.model;

import tiqueto.EjemploTicketMaster;

public class FanGrupo extends Thread {

	final WebCompraConciertos webCompra;
	int numeroFan;
	private String tabuladores = "\t\t\t\t";
	int entradasCompradas = 0;

	public FanGrupo(WebCompraConciertos web, int numeroFan) {
		super();
		this.numeroFan = numeroFan;
		this.webCompra = web;
	}

	@Override
	public void run() {
		while (entradasCompradas < EjemploTicketMaster.MAX_ENTRADAS_POR_FAN) {

			synchronized (webCompra) {

					if (webCompra.hayEntradas()) { //Verificamos si hay entradas
						mensajeFan("Esta en procesos para comprar entrada.");

						if (webCompra.comprarEntrada()) { //Vereficamos si se puede comprar
							entradasCompradas++;
							dimeEntradasCompradas();

						}

				} else {

					try {
						webCompra.wait(); // Espera a que el promotor reponga las entradas
					} catch (InterruptedException ie) {
						Thread.currentThread().interrupt();
						return;
					}
				}
			}

			try {
				Thread.sleep((int) (Math.random() * 2000) + 1000); // Dormir entre 1 y 3 segundos
			} catch (InterruptedException ie) {
				Thread.currentThread().interrupt();
				return;
			}
		}

		synchronized (webCompra) {

			if (--EjemploTicketMaster.NUM_FANS == 0) {
				WebCompraConciertos.todosFansCompletos = true;
				webCompra.notifyAll(); // Notificar que todos los fans han completado sus compras
			}
		}
	}

	public void dimeEntradasCompradas() {
		mensajeFan("Sólo he conseguido: " + entradasCompradas);
	}

	/**
	 * Método a usar para cada impresión por pantalla
	 * @param mensaje Mensaje que se quiere lanzar por pantalla
	 */
	private void mensajeFan(String mensaje) {
		System.out.println(System.currentTimeMillis() + tabuladores + "|"  +" Fan "+this.numeroFan +": " + mensaje);
	}
}