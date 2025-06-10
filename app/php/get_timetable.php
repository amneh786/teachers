<?php
header('Content-Type: application/json');

$conn = new mysqli("localhost", "root", "", "school1");

$user_id = $_GET['user_id'] ?? null;

if (!$user_id || !is_numeric($user_id)) {
    echo json_encode(["status" => "error", "message" => "Missing or invalid user_id"]);
    exit;
}

$teacher_stmt = $conn->prepare("SELECT id FROM teacher WHERE user_id = ?");
$teacher_stmt->bind_param("i", $user_id);
$teacher_stmt->execute();
$teacher_result = $teacher_stmt->get_result();

if ($teacher_result->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "No teacher found"]);
    exit;
}

$teacher_id = $teacher_result->fetch_assoc()['id'];

$sql = "
    SELECT 
        s.day AS day,
        CONCAT(s.start_time, ' - ', s.end_time) AS time_slot,
        subj.name AS subject_name,
        class.name AS class_name
    FROM schedule s
    JOIN subject subj ON s.subject_id = subj.id
    JOIN class ON s.class_id = class.id
    WHERE s.teacher_id = ?
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = [
        "day" => $row["day"],
        "time_slot" => $row["time_slot"],
        "subject_name" => $row["subject_name"],
        "class_name" => $row["class_name"]
    ];
}

echo json_encode($data);
$conn->close();
?>
