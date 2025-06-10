<?php
header("Content-Type: application/json");

$conn = new mysqli("localhost", "root", "", "school1");

$user_id = $_GET['user_id'] ?? null;

if (!$user_id || !is_numeric($user_id)) {
    echo json_encode(["status" => "error", "message" => "Missing or invalid user_id"]);
    exit;
}

// Step 1: Get teacher_id from user_id
$teacherStmt = $conn->prepare("SELECT id FROM teacher WHERE user_id = ?");
$teacherStmt->bind_param("i", $user_id);
$teacherStmt->execute();
$teacherResult = $teacherStmt->get_result();

if ($teacherResult->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "No teacher found for this user"]);
    exit;
}

$teacherRow = $teacherResult->fetch_assoc();
$teacher_id = $teacherRow['id'];

// Step 2: Get classes taught by this teacher
$query = "
    SELECT DISTINCT c.id, c.name
    FROM schedule s
    JOIN class c ON s.class_id = c.id
    WHERE s.teacher_id = ?
";

$stmt = $conn->prepare($query);
$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$classes = [];
while ($row = $result->fetch_assoc()) {
    $classes[] = $row;
}

echo json_encode(["status" => "success", "classes" => $classes]);
$conn->close();
?>
