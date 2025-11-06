# Club Los Andes

## Propuesta
Proponemos el desarrollo de un sistema de gestión para un **club deportivo**, orientado tanto a los socios como a los administradores. Los **socios** podrán acceder con su cuenta para realizar diversas acciones, como inscribirse en actividades como fútbol, tenis o hockey, alquilar canchas (por ejemplo, de pádel) para organizar partidos, alquilar salones para eventos. 
Por su parte, los **administradores** del sistema tendrán acceso a funcionalidades específicas para gestionar el estado de las cuotas sociales, verificar que la documentación de los socios esté al día y mantener actualizada la oferta de actividades, servicios, profesores, horarios de las actividades, salones, canchas y eventos. Este sistema busca centralizar y facilitar la administración general del club, mejorar la experiencia del socio y optimizar la organización interna mediante un entorno digital accesible y seguro.

---

## Modelo de Dominio
Podés consultar el modelo de dominio en el siguiente enlace:  
🔗 [Ver modelo de dominio](https://drive.google.com/file/d/1u3OEWpqnE02TRypaX-TFQbBMNAMUOwi_/view?usp=sharing)

---

## Regularidad

### ABMC (Altas, Bajas, Modificaciones y Consultas)

#### Entidades Simples
- **Usuario**  
- **Actividad**  
- **Cancha**  
- **Profesor**

#### Entidades Dependientes
- **Horario**  
- **Inscripción**

---

### Casos de Uso NO-ABMC
- No se podrá realizar una inscripción a una actividad **si no hay cupo disponible**.  
- Se debe **validar que un socio no pueda inscribirse** a una actividad **a la que ya se encuentra inscripto**.

---

### Listados
- **Listado Complejo:** Actividades a las que un usuario está inscripto hasta la fecha.

---

## Integrantes 
- María de los Ángeles Arfuso, 51454
- Regina Diodati, 50473
- Delfina Mansilla, 50353
- Francesca Maurutto, 51752


