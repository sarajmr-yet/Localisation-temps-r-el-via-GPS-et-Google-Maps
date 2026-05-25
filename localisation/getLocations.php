<?php
header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["ok" => false, "message" => "Methode non autorisee"]);
    exit;
}

require_once __DIR__ . '/repository/GeoPointRepository.php';

try {
    $repo      = new GeoPointRepository();
    $locations = $repo->fetchAll();
    echo json_encode(["ok" => true, "locations" => $locations]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["ok" => false, "message" => $e->getMessage()]);
}
