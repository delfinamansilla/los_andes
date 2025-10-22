CREATE USER 'los_andes_user'@'%' IDENTIFIED BY 'lasnenas';
GRANT SELECT, INSERT, UPDATE, DELETE ON los_andes.* TO 'los_andes_user'@'%';
