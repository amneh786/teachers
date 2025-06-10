<?php
header("Content-Type: application/json; charset=UTF-8");
ini_set('display_errors', 1);
error_reporting(E_ALL);

$conn = new mysqli("localhost", "root", "", "school1");
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "DB connection failed"]);
    exit;
}

$input = file_get_contents("php://input");
$data = json_decode($input, true);

if (!isset($data['attendance']) || !is_array($data['attendance'])) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Missing or invalid attendance data"]);
    exit;
}

$stmt = $conn->prepare("INSERT INTO attendance (student_id, class_id, homeroom_teacher_id, date, status) VALUES (?, ?, ?, CURDATE(), ?)");

foreach ($data['attendance'] as $entry) {
    $name = $entry['name'] ?? null;
    $class_id = $entry['class_id'] ?? null;
    $user_id = $entry['user_id'] ?? null;
    $status = $entry['status'] ?? 'Absent';

    if (!$name || !$class_id || !$user_id) continue;

    $student_query = $conn->prepare("
        SELECT s.id 
        FROM student s
        JOIN user u ON s.user_id = u.id
        WHERE u.name = ? AND s.class_id = ?
    ");
    $student_query->bind_param("si", $name, $class_id);
    $student_query->execute();
    $student_result = $student_query->get_result();
    $student_row = $student_result->fetch_assoc();
    $student_id = $student_row['id'] ?? null;
    $student_query->close();

    if (!$student_id) continue;

    $teacher_query = $conn->prepare("SELECT id FROM teacher WHERE user_id = ?");
    $teacher_query->bind_param("i", $user_id);
    $teacher_query->execute();
    $teacher_result = $teacher_query->get_result();
    $teacher_row = $teacher_result->fetch_assoc();
    $teacher_id = $teacher_row['id'] ?? null;
    $teacher_query->close();

    if (!$teacher_id) continue;

    $stmt->bind_param("iiis", $student_id, $class_id, $teacher_id, $status);
    $stmt->execute();
}

$stmt->close();
$conn->close();

echo json_encode(["status" => "success"]);
?>
