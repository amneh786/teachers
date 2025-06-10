<?php
header("Content-Type: application/json");
$conn = new mysqli("localhost", "root", "", "school1");

$user_id = $_GET['user_id'] ?? null;
$subject_name = $_GET['subject_name'] ?? null;

if (!$user_id || !$subject_name) {
    echo json_encode(["status" => "error", "message" => "Missing parameters"]);
    exit;
}

// get teacher_id from user_id
$teacherStmt = $conn->prepare("SELECT id FROM teacher WHERE user_id = ?");
$teacherStmt->bind_param("i", $user_id);
$teacherStmt->execute();
$teacherResult = $teacherStmt->get_result();

if ($teacherResult->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "Teacher not found"]);
    exit;
}

$teacherRow = $teacherResult->fetch_assoc();
$teacher_id = $teacherRow['id'];

$sql = "
    SELECT 
        o.title,
        o.description,
        c.name AS class_name,
        s.name AS subject_name
    FROM outline o
    JOIN subject s ON o.subject_id = s.id
    JOIN schedule sch ON sch.subject_id = o.subject_id AND sch.teacher_id = o.teacher_id
    JOIN class c ON c.id = sch.class_id
    WHERE o.teacher_id = ? AND s.name = ?
    GROUP BY o.id, c.id
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("is", $teacher_id, $subject_name);
$stmt->execute();
$result = $stmt->get_result();

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode(["status" => "success", "data" => $data]);
$conn->close();

