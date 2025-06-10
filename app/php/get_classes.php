<?php
header("Content-Type: application/json");

$conn = new mysqli("localhost", "root", "", "school1");

$user_id = $_GET['user_id'] ?? null;
if (!$user_id) {
    echo json_encode(["status" => "error", "message" => "Missing user_id"]);
    exit;
}

$stmt = $conn->prepare("SELECT id FROM teacher WHERE user_id = ?");
$stmt->bind_param("i", $user_id);
$stmt->execute();


if (!$stmt->fetch()) {
    echo json_encode(["status" => "error", "message" => "Teacher not found"]);
    $stmt->close();
    exit;
}
$stmt->close();

$sql = "
    SELECT c.name AS class_name, s.name AS subject_name
    FROM teacher_class tc
    JOIN class c ON tc.class_id = c.id
    JOIN class_subject cs ON cs.class_id = c.id
    JOIN subject s ON cs.subject_id = s.id
    WHERE tc.teacher_id = $user_id
";

$stmt = $conn->prepare($sql);
$stmt->execute();
$result = $stmt->get_result();

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode(["status" => "success", "data" => $data]);
$conn->close();
