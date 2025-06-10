<?php
header("Content-Type: application/json");
$conn = new mysqli("localhost", "root", "", "school1");

$user_id = $_GET['user_id'] ?? null;
$class_name = $_GET['class_name'] ?? null;
$type = $_GET['type'] ?? null;

if (!$user_id || !$class_name || !$type) {
    echo json_encode(["status" => "error", "message" => "Missing parameters"]);
    exit;
}

$teacherStmt = $conn->prepare("SELECT id FROM teacher WHERE user_id = ?");
$teacherStmt->bind_param("i", $user_id);
$teacherStmt->execute();
$teacherResult = $teacherStmt->get_result();

if ($teacherResult->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "Teacher not found"]);
    exit;
}

$teacher_id = $teacherResult->fetch_assoc()['id'];

$query = "
    SELECT s.id AS student_id, u.name AS student_name, 
           COALESCE(m.mark, '') AS mark
    FROM student s
    JOIN user u ON s.user_id = u.id
    JOIN class c ON s.class_id = c.id
    JOIN schedule sch ON c.id = sch.class_id
    LEFT JOIN marks m ON m.student_id = s.id AND m.type = ? AND m.teacher_id = ? AND m.class_id = c.id
    WHERE c.name = ? AND sch.teacher_id = ?
";

$stmt = $conn->prepare($query);
$stmt->bind_param("sisi", $type, $teacher_id, $class_name, $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$students = [];
while ($row = $result->fetch_assoc()) {
    $students[] = $row;
}

echo json_encode(["status" => "success", "students" => $students]);
$conn->close();
?>
