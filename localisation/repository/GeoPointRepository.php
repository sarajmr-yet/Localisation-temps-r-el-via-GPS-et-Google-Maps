<?php
require_once __DIR__ . '/../contract/ICrud.php';
require_once __DIR__ . '/../model/GeoPoint.php';
require_once __DIR__ . '/../db/DatabaseHelper.php';

class GeoPointRepository implements ICrud {
    private $db;

    public function __construct() {
        $helper   = new DatabaseHelper();
        $this->db = $helper->getPdo();
    }

    public function insert($geoPoint) {
        $sql  = "INSERT INTO position (latitude, longitude, date, imei)
                 VALUES (:lat, :lng, :dat, :dev)";
        $stmt = $this->db->prepare($sql);
        $stmt->execute([
            ':lat' => $geoPoint->getLat(),
            ':lng' => $geoPoint->getLng(),
            ':dat' => $geoPoint->getRecordedAt(),
            ':dev' => $geoPoint->getDeviceId()
        ]);
        return $this->db->lastInsertId();
    }

    public function fetchAll() {
        $stmt = $this->db->prepare("SELECT * FROM position ORDER BY date DESC");
        $stmt->execute();
        return $stmt->fetchAll();
    }

    public function modify($obj)  {}
    public function remove($obj)  {}
    public function findById($id) {}
}
