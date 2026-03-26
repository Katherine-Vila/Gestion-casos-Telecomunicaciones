-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3306
-- Tiempo de generación: 23-03-2026 a las 00:52:13
-- Versión del servidor: 8.4.7
-- Versión de PHP: 8.3.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `gestion_cambios`
--
CREATE DATABASE IF NOT EXISTS `gestion_cambios` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `gestion_cambios`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `archivos`
--

DROP TABLE IF EXISTS `archivos`;
CREATE TABLE IF NOT EXISTS `archivos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `caso_id` int NOT NULL,
  `nombre_archivo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ruta` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo` enum('SOLICITUD','ANALISIS') COLLATE utf8mb4_unicode_ci NOT NULL,
  `usuario_subio_id` int NOT NULL,
  `fecha_subida` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `caso_id` (`caso_id`),
  KEY `usuario_subio_id` (`usuario_subio_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bitacoras`
--

DROP TABLE IF EXISTS `bitacoras`;
CREATE TABLE IF NOT EXISTS `bitacoras` (
  `id` int NOT NULL AUTO_INCREMENT,
  `caso_id` int NOT NULL,
  `fecha` date NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `porcentaje_avance` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `caso_id` (`caso_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `casos`
--

DROP TABLE IF EXISTS `casos`;
CREATE TABLE IF NOT EXISTS `casos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descripcion_solicitud` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_solicitud` date NOT NULL,
  `estado` enum('EN_ESPERA','RECHAZADO','EN_DESARROLLO','ESPERANDO_APROBACION','DEVUELTO','VENCIDO','FINALIZADO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `porcentaje_avance` int DEFAULT '0',
  `fecha_limite` date DEFAULT NULL,
  `fecha_puesta_produccion` date DEFAULT NULL,
  `analisis_descripcion` text COLLATE utf8mb4_unicode_ci,
  `argumento_rechazo` text COLLATE utf8mb4_unicode_ci,
  `observaciones_rechazo` text COLLATE utf8mb4_unicode_ci,
  `fecha_devolucion` date DEFAULT NULL,
  `departamento_id` int NOT NULL,
  `solicitante_id` int NOT NULL,
  `jefe_desarrollo_id` int DEFAULT NULL,
  `programador_id` int DEFAULT NULL,
  `probador_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `departamento_id` (`departamento_id`),
  KEY `solicitante_id` (`solicitante_id`),
  KEY `jefe_desarrollo_id` (`jefe_desarrollo_id`),
  KEY `programador_id` (`programador_id`),
  KEY `probador_id` (`probador_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `departamentos`
--

DROP TABLE IF EXISTS `departamentos`;
CREATE TABLE IF NOT EXISTS `departamentos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(3) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol` enum('ADMIN','JEFE_AREA','EMPLEADO','JEFE_DESARROLLO','PROGRAMADOR') COLLATE utf8mb4_unicode_ci NOT NULL,
  `departamento_id` int DEFAULT NULL,
  `jefe_desarrollo_id` int DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `departamento_id` (`departamento_id`),
  KEY `jefe_desarrollo_id` (`jefe_desarrollo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `archivos`
--
ALTER TABLE `archivos`
  ADD CONSTRAINT `archivos_ibfk_1` FOREIGN KEY (`caso_id`) REFERENCES `casos` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `archivos_ibfk_2` FOREIGN KEY (`usuario_subio_id`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `bitacoras`
--
ALTER TABLE `bitacoras`
  ADD CONSTRAINT `bitacoras_ibfk_1` FOREIGN KEY (`caso_id`) REFERENCES `casos` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `casos`
--
ALTER TABLE `casos`
  ADD CONSTRAINT `casos_ibfk_1` FOREIGN KEY (`departamento_id`) REFERENCES `departamentos` (`id`),
  ADD CONSTRAINT `casos_ibfk_2` FOREIGN KEY (`solicitante_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `casos_ibfk_3` FOREIGN KEY (`jefe_desarrollo_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `casos_ibfk_4` FOREIGN KEY (`programador_id`) REFERENCES `usuarios` (`id`),
  ADD CONSTRAINT `casos_ibfk_5` FOREIGN KEY (`probador_id`) REFERENCES `usuarios` (`id`);

--
-- Filtros para la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`departamento_id`) REFERENCES `departamentos` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `usuarios_ibfk_2` FOREIGN KEY (`jefe_desarrollo_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
