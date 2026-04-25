```mermaid

classDiagram
    direction TB

    namespace model {
        class Customer {
            <<record>>
            +String id
            +String nombre
            +String email
        }
        class Product {
            <<record>>
            +String sku
            +String nombre
            +double precioUnitario
            +ProductCategory categoria
        }
        class InvoiceItem {
            <<record>>
            +Product producto
            +int cantidad
            +calcularSubtotal() double
        }
        class Invoice {
            -String id
            -Customer cliente
            -List~InvoiceItem~ items
            -InvoiceStatus status
            -LocalDate fecha
            +getId() String
            +getCliente() Customer
            +equals() boolean
            +hashCode() int
            +toString() String
        }
        class InvoiceStatus {
            <<enumeration>>
            PENDING
            PAID
            DRAFT
        }
        class ProductCategory {
            <<enumeration>>
            SOFTWARE
            HARDWARE
            CONSULTORIA
            SOPORTE
            LICENCIA
        }
        class TaxStrategy {
            <<interface>>
            +calcular(subtotal) double
        }
        class Auditable {
            <<annotation>>
            +String descripcion
        }
    }

    namespace repository {
        class Repository~T~ {
            -List~T~ almacenamiento
            +guardar(elemento: T) void
            +obtenerTodos() List~T~
            +cargarDesde(fuente: List~? extends T~) void
            +exportarHacia(destino: List~? super T~) void
            +total() int
        }
    }

    namespace service {
        class InvoiceService {
            -List~Customer~ catalogoClientes
            -List~Product~ catalogoProductos
            -Repository~Invoice~ repositorio
            -TaxStrategy ivaStrategy
            +buscarClientePorId(id) Optional~Customer~
            +buscarProductoPorSku(sku) Optional~Product~
            +calcularSubtotal(items) double
            +calcularTotal(items) double
            +generarFactura(...) Invoice
            +filtrarPorFecha(...) List~Invoice~
        }
    }

    namespace view {
        class ConsoleView {
            -InvoiceService servicio
            -Scanner scanner
            +iniciar() void
            -mostrarMenu() void
        }
        class Main {
            +main(args: String[]) void
        }
    }

    %% Relaciones
    Invoice "*" --> "1" Customer : tiene
    Invoice "*" *-- "*" InvoiceItem : contiene
    InvoiceItem "*" --> "1" Product : referencia
    Invoice --> InvoiceStatus : estado
    Product --> ProductCategory : clasificacion
    
    InvoiceService --> Repository~Invoice~ : usa
    InvoiceService ..|> TaxStrategy : usa lambda
    
    ConsoleView --> InvoiceService : invoca
    Main --> ConsoleView : inicia