-- Base de données : localisation
CREATE DATABASE IF NOT EXISTS `localisation` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `localisation`;

CREATE TABLE `position` (
  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `latitude` double NOT NULL,
  `longitude` double NOT NULL,
  `date` datetime NOT NULL,
  `imei` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Données de démonstration
INSERT INTO `position` (`latitude`, `longitude`, `date`, `imei`) VALUES
(48.8566, 2.3522, '2024-11-10 09:15:00', 'a1b2c3d4e5f60001'),
(48.8570, 2.3530, '2024-11-10 09:16:02', 'a1b2c3d4e5f60001'),
(48.8575, 2.3545, '2024-11-10 09:17:10', 'a1b2c3d4e5f60001'),
(48.8580, 2.3560, '2024-11-10 09:18:25', 'a1b2c3d4e5f60001'),
(48.8585, 2.3575, '2024-11-10 09:19:40', 'a1b2c3d4e5f60001');
