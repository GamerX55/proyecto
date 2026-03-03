Hola amiguitos, el deivid es la mera vrg
# Sistema Abarrotera

Sistema de gestión para tienda de abarrotes desarrollado en Java Swing.

## Módulos

- **Compras** - Registro, modificación, eliminación y búsqueda de compras
- **Clientes** - Alta, edición, búsqueda de clientes con indicador de estatus crediticio
- **Créditos** - Otorgamiento, pagos, modificación de créditos con sistema de colores (verde/amarillo/rojo)
- **Reportes** - Estadísticas de compras, proveedores y ganancias estimadas

## Estructura del Proyecto

```
SistemaAbarrotera/
├── src/
│   ├── modelo/          # Clases de datos (Cliente, Compra, Credito)
│   ├── servicio/        # Lógica de negocio (ClienteService, CompraService, CreditoService)
│   └── ui/              # Interfaz gráfica Swing (paneles y ventana principal)
├── bin/                 # Archivos .class compilados
├── compilar.bat         # Script de compilación
├── ejecutar.bat         # Script de ejecución
└── README.md
```

## Compilar y Ejecutar

```batch
compilar.bat
ejecutar.bat
```

O manualmente:
```batch
javac -encoding UTF-8 -d bin -sourcepath src src\modelo\*.java src\servicio\*.java src\ui\*.java
java -cp bin ui.InterfazPrincipal
```

## Requisitos

- Java JDK 8 o superior
