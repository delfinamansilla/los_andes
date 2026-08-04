# Club Los Andes

## Propuesta
Proponemos el desarrollo de un sistema de gestión para un club deportivo, orientado tanto a los socios como a los administradores. Los socios podrán acceder con su cuenta para realizar diversas acciones, como inscribirse en actividades (fútbol, tenis, hockey), alquilar canchas y salones para eventos.
Por su parte, los administradores cuentan con herramientas avanzadas para centralizar la administración: gestión de cuotas sociales, control de morosidad, mantenimiento de infraestructura (canchas/salones), organización de torneos y monitoreo del club mediante informes estadísticos de ocupación y recaudación. Este sistema optimiza la organización interna mediante un entorno digital accesible, seguro y con trazabilidad de datos.

---

## Modelo de Dominio
Puede consultar el modelo de dominio en el siguiente enlace:  
🔗 [Ver modelo de dominio]([https://drive.google.com/file/d/1u3OEWpqnE02TRypaX-TFQbBMNAMUOwi_/view?usp=sharing](https://app.diagrams.net/#G1u3OEWpqnE02TRypaX-TFQbBMNAMUOwi_#%7B%22pageId%22%3A%225Ts5IIsQ6J3-bFJRZr1w%22%7D)

---
### Entidades del sistema

#### Entidades Simples
- **Usuario**
- **Cuota**
- **Actividad**  
- **Cancha**  
- **Profesor**
- **Salón**
- **Partido**
- **Configuracion**

#### Entidades Dependientes y de Proceso
- **MontoCuota**
- **PagoCuota**
- **Inscripción**
- **Horario**
- **Alquiler_cancha**
- **Alquiler_salon**
- **Reserva_pendiente**
- **Prereserva_cancha**
- **Recuperacion_pass**
- **InformeRecaudacion**
- **InformeOcupacion**
  
#### Entidades de Reporte
- **InformeRecaudacion**: Entidad utilizada para consolidar datos de pagos, usuarios y montos para el análisis financiero.
- **InformeOcupacion**: Entidad que unifica los alquileres de toda la infraestructura (Canchas y Salones) para medir el uso del club.

---

### ABMC (Altas, Bajas, Modificaciones y Consultas)
- **Usuario**:
  - Alta (Registro) con validación de DNI único.
  - Modificación de datos personales.
  - Cambio de contraseña.
  - Consulta de perfil.
- **Salón**: ABMC completa (El administrador puede crear, editar, eliminar y consultar salones).
- **Cancha**: ABMC completa (El administrador puede crear, editar, eliminar y consultar canchas).
- **Actividad**: ABMC completa (El administrador puede crear, editar, eliminar y consultar actividades).
- **Horario**: ABMC completa (El administrador puede crear, editar, eliminar y consultar los horarios de cada actividad).
- **Partido**: ABMC completa (El administrador puede crear, editar, eliminar y consultar partidos).
- **Profesor**: ABMC completa (El administrador puede crear, editar, eliminar y consultar profesores).
- **Configuración del Club**: ABMC del historial de cupos y parámetros globales.
---

### Casos de Uso No-ABMC y Lógica de Negocio

#### Gestión de Accesos y Usuarios
- **Registro de usuario**: Se valida estrictamente que el DNI no exista ya en el sistema.
- Login socio:
  - Se valida que el usuario no tenga dos cuotas impagas consecutivas para permitir el acceso.
  - Control para asegurar que no se generen más de dos cuotas por mes.
- **Recuperación de contraseña**:
  - Se valida que el mail ingresado exista en la base de datos.
  - Una vez validado, se envía un correo que deriva al formulario de cambio de contraseña.
- **Cumpleaños en el inicio**: Al loguearse, el sistema calcula si es el cumpleaños del usuario. De ser así, muestra una animación de globos y un mensaje por 15 segundos.

#### Gestión de Alquileres (Salón y Cancha) - Caso de Uso Complejo
Este flujo involucra eventos en momentos distintos:
1. El socio consulta la disponibilidad por fecha.
2. El sistema bloquea los horarios reservados y solo permite seleccionar horarios libres.
3. Antes de confirmarse, se envía un mail de verificación al usuario.
4. El alquiler se registra efectivamente en la base de datos solo tras la confirmación por correo.

#### Gestión de Actividades y Horarios
- **Creación de Horario**: Al crear un horario, se valida que ni el Profesor ni la Cancha asignada estén ocupados en ese momento.
- **Inscripción a actividad**: Se valida que el usuario no esté ya inscripto en esa actividad previamente.

#### Gestión de Pagos
- **Pago con Mercado Pago**: Si la cuota no está paga, se genera un QR con el link de pago para que el socio abone.
- **Pago en Efectivo (Administración)**:
  - El administrador ingresa al listado de clientes y deja constancia del pago.
  - Una vez registrado, el sistema envía automáticamente un mail con el comprobante de pago y los datos pertinentes.

 #### Inteligencia de Negocio e Informes
 El sistema recolecta datos de múltiples tablas para generar conocimiento:
- **Procesamiento de Estadísticas**: Lógica para agrupar pagos por período y por tipo de cuota, y ocupación por recurso.
- **Gráficos Dinámicos**: Integración de Recharts en el frontend para visualizar tendencias sin depender de documentos externos.
- **Exportación Profesional**: Generación de archivos PDF utilizando librerías de bajo nivel, incluyendo logotipos institucionales y gráficos estadísticos.

#### Gestión de Socios y Cupo Dinámico
- **Validación de Capacidad**: Antes de cada registro o aprobación de socio, el sistema consulta el cupo máximo actual y lo contrasta con los socios activos en tiempo real.
- **Auditoría de Configuración**: Los cambios en el cupo del club no se sobrescriben, sino que generan un nuevo registro en el historial para permitir el seguimiento de las decisiones administrativas.
- **Seguridad**: Recuperación de contraseña mediante tokens de un solo uso con expiración temporal.

### Listados

#### Listados Simples
- **Historial de alquileres de salón (Admin)**: Muestra reservas pasadas y futuras sin filtros.
- **Listado de Socios**: Muestra todos los socios e incluye botones para ver sus cuotas, alquileres de salón o alquileres de cancha.
- **Listado de Actividades**: Listado general con botón de inscripción.
- **Historial de Cuotas**: Muestra las cuotas de cada socio (pagas o no pagas) indicando estado, monto e intereses si corresponde.
- **Listado de todos los Partidos**: Vista general de partidos.

#### Listados Complejos
- **Mis Alquileres Futuros (Socio)**: Muestra solo los alquileres con fecha mayor a la actual, con datos del salón/cancha y del alquiler.
- **Mis Inscripciones**: Listado de las actividades a las que el socio está inscripto.
- **Listado de Cuotas Filtrable**: Permite filtrar por estado: “pagas”, “pendientes” o “todas”.
- **Buscador de socios por DNI**: Filtra dinámicamente el listado mientras se escribe.
- **Alquileres de Salón de un socio**: Historial específico por usuario.
- **Alquileres de Cancha de un socio**: Historial específico por usuario.
- **Horarios de una actividad**: Detalle de días y horas.
- **Partidos Semanales**: Listado de partidos filtrados por fecha, mostrando únicamente los de la semana en curso.
- **Incripciones por actividad**: Listado de alumnos inscriptos a una determinada actividad.

---

## Integrantes 
- María de los Ángeles Arfuso, 51454
- Regina Diodati, 50473
- Delfina Mansilla, 50353
- Francesca Maurutto, 51752


