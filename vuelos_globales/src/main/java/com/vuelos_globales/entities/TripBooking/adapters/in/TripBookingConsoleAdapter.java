package com.vuelos_globales.entities.TripBooking.adapters.in;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.vuelos_globales.entities.BookingStatus.domain.BookingStatus;
import com.vuelos_globales.entities.Customer.domain.Customer;
import com.vuelos_globales.entities.TripBooking.application.TripBookingService;
import com.vuelos_globales.entities.TripBooking.domain.TripBooking;
import com.vuelos_globales.modules.ConsoleUtils;
import com.vuelos_globales.entities.FlightFares.domain.FlightFares;
import com.vuelos_globales.entities.Trip.domain.Trip;
import com.vuelos_globales.entities.FlightConnection.domain.FlightConnection;

public class TripBookingConsoleAdapter {
    Scanner sc = new Scanner(System.in);

    private final TripBookingService tripBookingService;

    public TripBookingConsoleAdapter(TripBookingService tripBookingService) {
        this.tripBookingService = tripBookingService;
    }

    public void createTripBooking() {
        String rta = "S";
    
        while (rta.equalsIgnoreCase("S")) {
            ConsoleUtils.limpiarConsola();
            System.out.println("*************** REGISTRAR RESERVA DE VUELO ***************");
            String id = "";
            boolean isValidId = false;
    
            while (!isValidId) {
                System.out.println("[*] ASIGNE UN ID A LA RESERVA DE VUELO A CREAR (MÁXIMO 5 CARACTERES): ");
                id = sc.nextLine();
    
                if (id.length() <= 5) {
                    isValidId = true;
                } else {
                    System.out.println("[!] ID NO VÁLIDO. DEBE SER DE MÁXIMO 5 CARACTERES.");
                }
            }
            final String finalId = id;
            Optional<TripBooking> tripBooking = tripBookingService.getTripBookingById(id);
            ConsoleUtils.limpiarConsola();
            tripBooking.ifPresentOrElse(
                t -> {
                    System.out.println(MessageFormat.format("[!] LA RESERVA CON ID ({0}) YA ESTÁ OCUPADA.", t.getId()));
                },
                () -> {
                    ConsoleUtils.esperarEntrada();
                    System.out.println("*************** REGISTRAR RESERVA DE VUELO ***************");
    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate fechaReserva = null;
                    boolean isActiveDate = false;
    
                    while (!isActiveDate) {
                        System.out.println("\n[*] INGRESE LA FECHA DE RESERVA DE VUELO (dd-MM-yyyy)");
                        System.out.print("INGRESE LA FECHA DE LA RESERVA DE VUELO: ");
                        String newDate = sc.nextLine();
    
                        try {
                            fechaReserva = LocalDate.parse(newDate, formatter);
                            isActiveDate = true;
                        } catch (DateTimeParseException e) {
                            System.out.println("Fecha ingresada no válida. Use el formato dd-MM-yyyy.");
                        }
                    }
    
                    // CLIENTE

                    System.out.println("\n[*] INGRESE EL ID DEL CLIENTE: ");
                    String newIdCustomer = sc.nextLine();
    
                    Optional<Customer> isValidCustomer = tripBookingService.getCustomerById(newIdCustomer);
    
                    if (isValidCustomer.isEmpty()) {
                        System.out.println("[!] ID DE CLIENTE NO VALIDO. EL CLIENTE NO EXISTE.");
                        return; 
                    }
                    
                    // VUELO 

                    System.out.println("\n[*] INGRESE EL ID DEL VUELO: ");
                    String newIdTrip = sc.nextLine();
    
                    Optional<Trip> isValidTrip= tripBookingService.getTripById(newIdTrip);
    
                    if (isValidTrip.isEmpty()) {
                        System.out.println("[!] ID DE VUELO NO VALIDO. EL VUELO NO EXISTE.");
                        return; 
                    }

                    // ESTADO DE RESERVA

                    List<BookingStatus> bookingStatuses = tripBookingService.getAllBookingStatuses();
                    if (bookingStatuses.isEmpty()) {
                        System.out.println("[!] NO SE ENCONTRARON ESTADOS DE RESERVA DISPONIBLES.");
                        return; 
                    }

                    System.out.println("\n[+] ESTADOS DE RESERVA DISPONIBLES:");
                    for (BookingStatus status : bookingStatuses) {
                        System.out.println("- ID: " + status.getId() + ", ESTADO: " + status.getBookingStatus());
                    }
    
                    System.out.println("\n[*] INGRESE EL ID DEL ESTADO DE RESERVA: ");
                    int newIdStatus = sc.nextInt();

                    Optional<BookingStatus> isValidBStatus= tripBookingService.getBookingStatusById(newIdStatus);
    
                    if (isValidBStatus.isEmpty()) {
                        System.out.println("[!] ID DE VUELO NO VALIDO. EL VUELO NO EXISTE.");
                        return; 
                    }
    
                    TripBooking newTripBooking = new TripBooking(finalId, fechaReserva, newIdTrip, newIdStatus, newIdCustomer);
                    tripBookingService.createTripBooking(newTripBooking);
                    System.out.println("RESERVA DE VUELO CREADA EXITOSAMENTE!");
                    ConsoleUtils.esperarEntrada();
                }
            );
    
            System.out.println("[?] DESEA AÑADIR OTRA RESERVA DE VUELO? [S] - SI | [INGRESE CUALQUIER TECLA] - NO");
            rta = sc.nextLine();
        }
    }
    
    

    public void searchTripBooking() {
        ConsoleUtils.limpiarConsola();
        System.out.println("*************** BUSCAR RESERVA DE VUELO ***************");
        System.out.println("[?] DIGITE EL NUMERO DE LA OPCION QUE DESEA REALIZAR:");
        System.out.println("[1]. CONSULTAR LA RESERVA DE VUELO POR ID");
        System.out.println("[2]. REVISAR EL TRAYECTO POR ID");
        System.out.println("[3]. REVISAR LA TARIFA DE VUELO POR ID");
        System.out.println("SELECCIONE UNA OPCION: ");
        
        String option = sc.nextLine();
        
        switch (option) {
            case "1":
                searchTripBookingById();
                break;
            case "2":
                searchFlightConnection();
                break;
            case "3":
                searchFlightFareById();
                break;
            default:
                ConsoleUtils.limpiarConsola();
                System.out.println("[!] OPCION NO VALIDA. POR FAVOR SELECCIONE 1, 2 O 3.");
                sc.nextLine();
                break;
        }
        
        ConsoleUtils.limpiarConsola();
        System.out.println("[*] PRESIONE CUALQUIER TECLA PARA CONTINUAR...");
        sc.nextLine();
    }
    
    private void searchTripBookingById() {
        List<TripBooking> tripBookings = tripBookingService.getAllTripBookings();
    
        if (tripBookings.isEmpty()) {
            ConsoleUtils.limpiarConsola();
            System.out.println("[!] NO HAY NINGUNA RESERVA DE VUELO REGISTRADA");
            sc.nextLine();
        } else {
            ConsoleUtils.limpiarConsola();
            System.out.println("*************** BUSCAR RESERVA DE VUELO ***************");
            System.out.println("[?] INGRESE EL ID DE LA RESERVA DE VUELO A BUSCAR: ");
            String findId = sc.nextLine();
    
            Optional<TripBooking> tripBooking = tripBookingService.getTripBookingById(findId);
            tripBooking.ifPresentOrElse(
                t -> {
                    ConsoleUtils.limpiarConsola();
                    System.out.println("*************** RESERVA DE VUELO ***************");
                    System.out.println(MessageFormat.format(
                        "[*] ID : {0}\n[*] FECHA DE RESERVA : {1}\n[*] ID DE VUELO: {2}\n[*] ID DE ESTADO DE RESERVA: {3}\n[*] ID DEL CLIENTE: {4}", t.getId(), t.getBookingDate(), t.getIdTrip(), t.getIdBookingStatus(), t.getIdCustomer())); 
                    sc.nextLine();
                },
                () -> {
                    ConsoleUtils.limpiarConsola();
                    System.out.println("[!] RESERVA DE VUELO NO ENCONTRADA");
                    sc.nextLine();
                }
            );
        }
        ConsoleUtils.limpiarConsola();
        System.out.println("[*] PRESIONE CUALQUIER TECLA PARA CONTINUAR...");
        sc.nextLine();
    }
    
    private void searchFlightFareById() {
        List<FlightFares> flightFares = tripBookingService.getAllFlightFares();
    
        if (flightFares.isEmpty()) {
            ConsoleUtils.limpiarConsola();
            System.out.println("[!] NO HAY NINGUNA TARIFA DE VUELO REGISTRADA");
            sc.nextLine();
        } else {
            ConsoleUtils.limpiarConsola();
            System.out.println("*************** BUSCAR TARIFA DE VUELO ***************");
            System.out.println("[?] INGRESE EL ID DEL VUELO DE VUELO A BUSCAR: ");
            String findIdFlare = sc.nextLine();  
    
            Optional<FlightFares> flightFare = tripBookingService.getFlightFaresById(findIdFlare);
            flightFare.ifPresentOrElse(
                tc -> {
                    ConsoleUtils.limpiarConsola();
                    System.out.println("*************** TARIFA DE VUELO ***************");
                    System.out.println(MessageFormat.format("[*] ID : {0}\n[*] DESCRIPCION: {1}\n[*] VALOR: {2}\n", tc.getId(), tc.getDescription(), tc.getValue()));
                    sc.nextLine();
                },
                () -> {
                    ConsoleUtils.limpiarConsola();
                    System.out.println("[!] TARIFA DE VUELO NO ENCONTRADA");
                    sc.nextLine();
                }
            );
        }
        ConsoleUtils.limpiarConsola();
        System.out.println("[*] PRESIONE CUALQUIER TECLA PARA CONTINUAR...");
        sc.nextLine();
    }
    
    private void searchFlightConnection() {
        ConsoleUtils.limpiarConsola();
    
        List<FlightConnection> flightConnections = tripBookingService.getAllFlightConnections();
        if (flightConnections.isEmpty()) {
            System.out.println("[!] NO HAY NINGUN TRAMO REGISTRADO");
        } else {
            System.out.println("[***] CONSULTAR INFORMACION DE TRAYECTO [***]");
            System.out.println("\n[*] INGRESE EL ID DEL VUELO AL QUE DESEA REVISAR EL TRAYECTO");
            String findIdTrip = sc.nextLine();
    
            Optional<FlightConnection> connection = tripBookingService.getFlightCOnnectionByTrip(findIdTrip);
    
            connection.ifPresentOrElse(
                f -> {
                    System.out.println(MessageFormat.format("\n[*] ID : {0}\n[*] ORDEN : {1}\n[*] VUELO : {2}\n[*] AVION : {3}\n[*] AEREOPUERTO SALIDA : {4}\n[*] AEREOPUERTO LLEGADA : {5}", f.getId(), f.getConnectionOrder(), f.getIdTrip(), f.getIdPlane(), f.getIdAirportA(), f.getIdArportB()));
                },
                () -> {
                    System.out.println("\n[!] NO SE ENCONTRO NINGUNA CONEXION RELACIONADA CON ESTE VUELO\n");
                }
            );
        }
        ConsoleUtils.esperarEntrada();
    }
    
           
    


    public void updateTripBooking(){
        ConsoleUtils.limpiarConsola();
        List<TripBooking> tripBookings = tripBookingService.getAllTripBookings();

        if(tripBookings.isEmpty()){

            System.out.println("[!]  NO HAY RESERVA DE VUELO REGISTRADAS");

        }  else {

            System.out.println("[*]  INGRESE EL ID DEL RESERVA DE VUELO A EDITAR\n\n");
            String findId = sc.nextLine();

            Optional<TripBooking> tripBooking = tripBookingService.getTripBookingById(findId);
            tripBooking.ifPresentOrElse(
            t -> {
                System.out.println(MessageFormat.format("[*] ID : {0}\n[*] FECHA DE RESERVA : {1}\n[*] ID DE VUELO: {2}\n[*] ID DE ESTADO DE RESERVA: {3}\n[*] ID DEL CLIENTE: {4}", t.getId(), t.getBookingDate() + t.getIdTrip(), t.getIdBookingStatus(), t.getIdCustomer()));

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate fechaReserva = null;
                    boolean isActiveDate = false;
                    String newDate = "";

                    while (!isActiveDate) {
                        System.out.println("\n[*] INGRESE LA FECHA DE RESERVA DE VUELO (dd-MM-yyyy)");
                        System.out.print("INGRESE LA FECHA DE RESERVA DE VUELO: ");
                        newDate = sc.nextLine();

                        try {
                            fechaReserva = LocalDate.parse(newDate, formatter);
                            isActiveDate = true;
                        } catch (DateTimeParseException e) {
                            System.out.println("Fecha ingresada no válida. Use el formato dd-MM-yyyy.");
                        }
                    }
                    System.out.println("\n[*]  INGRESE EL ID DEL VUELO: ");
                    String newIdTrip = sc.nextLine();

                    System.out.println("\n[*]  INGRESE EL ID DEL ESTADO DE RESERVA: ");
                    int newIdStatus = sc.nextInt();

                    System.out.println("\n[*]  INGRESE EL ID DEL CLIENTE: ");
                    String newIdCustomer = sc.nextLine();

                    TripBooking newTripBooking = new TripBooking(findId, fechaReserva, newIdTrip, newIdStatus, newIdCustomer);
                    tripBookingService.updateTripBooking(newTripBooking);
            },
            () -> System.out.println("[!]  RESERVA DE VUELO NO ENCONTRADA")
        );
        System.out.println("[*]  PRESIONE CUALQUIER TECLA PARA CONTINUAR...");
        sc.nextLine();
        }
    }


    public void deleteTripBooking(){
        ConsoleUtils.limpiarConsola();
        List<TripBooking> tripBookings = tripBookingService.getAllTripBookings();
        if(tripBookings.isEmpty()){
            ConsoleUtils.limpiarConsola();
            System.out.println("[!] NO HAY NINGUNA RESERVA DE VUELO REGISTRADA");
            sc.nextLine();
        } else {
            ConsoleUtils.limpiarConsola();
            System.out.println("[*]  INGRESE EL ID DE LA RESERVA DE VUELO A ELIMINAR\n\n");
            String findId = sc.nextLine();


            Optional<TripBooking> tripBooking = tripBookingService.getTripBookingById(findId);
            tripBooking.ifPresentOrElse(
                c -> {
                    tripBookingService.deleteTripBooking(findId);
                    System.out.println("[!] RESERVA DE VUELO ELIMINADA CORRECTAMENTE.");
                    sc.nextLine();
                },
                () -> {
                    System.out.println("[!]  RESERVA DE VUELO NO ENCONTRADO");

                }
            );
            System.out.println("[*]  PRESIONE CUALQUIER TECLA PARA CONTINUAR...");
            sc.nextLine();
        }
    }

    public void getAllTripBookings() {
        List<TripBooking> TripBookings = tripBookingService.getAllTripBookings();
        
        if (TripBookings.isEmpty()) {
            ConsoleUtils.limpiarConsola();
            System.out.println("[!] NO HAY NINGUNA RESERVA DE VUELO REGISTRADO");
            sc.nextLine();
        } else {
            ConsoleUtils.limpiarConsola();
            tripBookingService.getAllTripBookings().forEach(t -> {
                System.out.println(MessageFormat.format("[*] ID : {0}\n[*] FECHA DE RESERVA : {1}\n[*] ID DE VUELO: {2}\n[*] ID DE ESTADO DE RESERVA: {3}\n[*] ID DEL CLIENTE: {4}", t.getId(), t.getBookingDate() + t.getIdTrip(), t.getIdBookingStatus(), t.getIdCustomer()));
            });
            System.out.println("[*]  PRESIONE CUALQUIER TECLA PARA CONTINUAR...");
            sc.nextLine();
        }
    }
}