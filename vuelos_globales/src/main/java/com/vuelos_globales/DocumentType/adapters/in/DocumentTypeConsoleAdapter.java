package com.vuelos_globales.DocumentType.adapters.in;

import java.util.Scanner;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import com.vuelos_globales.ConsoleUtils;
import com.vuelos_globales.DocumentType.domain.DocumentType;
import com.vuelos_globales.DocumentType.application.DocumentTypeService;
import com.vuelos_globales.DocumentType.domain.DocumentType;

public class DocumentTypeConsoleAdapter {
    Scanner sc = new Scanner(System.in);

    private final DocumentTypeService docTypeService;

    public DocumentTypeConsoleAdapter(DocumentTypeService docTypeService) {
        this.docTypeService = docTypeService;
    }

    public void createDocType() {
        String rta = "S";

        while (rta.equalsIgnoreCase("S")) {
            ConsoleUtils.limpiarConsola();
            try {
                System.out.println("*************** REGISTRAR TIPO DE DOCUMENTO ***************");
                System.out.println("[*] INGRESE EL ID DEL TIPO DE DOCUMENTO A CREAR: ");
                int id = Integer.parseInt(sc.nextLine().trim());

                Optional<DocumentType> docType = docTypeService.getDocumentTypeById(id);
                docType.ifPresentOrElse(
                    d -> {
                        System.out.println(MessageFormat.format("[!] EL ID (0) YA ESTA OCUPADO.", d.getId()));
                    },
                    () -> {
                        ConsoleUtils.limpiarConsola();
                        System.out.println("*************** REGISTRAR TIPO DE DOCUMENTO ***************");
                
                        System.out.println("[*] INGRESE EL NOMBRE DEL TIPO DE DOCUMENTO: ");
                        String docTypeName = sc.nextLine();
                
                        DocumentType newDocumentType = new DocumentType(id, docTypeName);
                        docTypeService.createDocumentType(newDocumentType);
                    });

                    System.out.println("[?] DESEA AÑADIR OTRO TIPO DE DOCUMENTO? [S] - SI | [INGRESE CUALQUIER TECLA] - NO");
                    rta = sc.nextLine();
            } catch (NumberFormatException e) {
                ConsoleUtils.limpiarConsola();
                System.out.println("[!] Ingresaste una opción inválida.");
                sc.nextLine();
            }
        }
    }

        public void searchDocumentType() {
        List<DocumentType> documentTypes = docTypeService.getAllDocumentTypes();
        
        if (documentTypes.isEmpty()) {
            ConsoleUtils.limpiarConsola();
            System.out.println("[!] NO HAY NINGUN TIPO DE DOCUMENTO REGISTRADO");
            sc.nextLine();
        } else {
            try {
                ConsoleUtils.limpiarConsola();
                System.out.println("*************** BUSCAR TIPO DE DOCUMENTO ***************");
                System.out.println("[*] INGRESE EL ID DEL TIPO DE DOCUMENTO A BUSCAR: ");
                int id = Integer.parseInt(sc.nextLine().trim());
    
                Optional<DocumentType> documentType = docTypeService.getDocumentTypeById(id);
                documentType.ifPresentOrElse(
                    d -> {
                        ConsoleUtils.limpiarConsola();
                        System.out.println("*************** TIPO DE DOCUMENTO ***************");
                        System.out.println(MessageFormat.format("[*] ID : {0}\n[*] TIPO DE DOCUMENTO :", d.getId(), d.getDocumentType()));
                        sc.nextLine();
                    },
                    () -> {
                        ConsoleUtils.limpiarConsola();
                        System.out.println("[!]  TIPO DE DOCUMENTO NO ENCONTRADO");
                        sc.nextLine();
                    });
                    ConsoleUtils.limpiarConsola();
                    System.out.println("[*]  PRESIONE CUALQUIER TECLA PARA CONTINUAR...");
                    sc.nextLine();
            } catch (NumberFormatException e) {
                ConsoleUtils.limpiarConsola();
                System.out.println("[!] INGRESASTE UNA OPCION INVALIDA.");
                sc.nextLine();
            }
            
        }
    }
}
