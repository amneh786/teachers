<?php
header("Content-Type: application/json");

$conn = new mysqli("localhost", "root", "", "school1");

if ($conn->connect_error) {
    die(json_encode(["error" => "Database connection failed"]));
}

if (isset($_GET['assignment_title'])) {
    $assignment_title = $_GET['assignment_title'];

    // Step 1: Get the assignment ID
    $stmt = $conn->prepare("SELECT id FROM assignment WHERE title = ?");
    $stmt->bind_param("s", $assignment_title);
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();

    if ($row) {
        $assignment_id = $row['id'];

        // Step 2: Get students who submitted
        $stmt1 = $conn->prepare("SELECT user.name FROM submission
                                 JOIN user ON submission.student_id = user.id 
                                 WHERE submission.assignment_id = ?");
        $stmt1->bind_param("i", $assignment_id);
        $stmt1->execute();
        $submitted_result = $stmt1->get_result();
        $submitted = [];
        while ($s_row = $submitted_result->fetch_assoc()) {
            $submitted[] = $s_row;
        }

        // Step 3: Get students who did NOT submit
        $stmt2 = $conn->prepare("SELECT name FROM user WHERE role = 'student' AND id NOT IN 
                                 (SELECT student_id FROM submission WHERE assignment_id = ?)");
        $stmt2->bind_param("i", $assignment_id);
        $stmt2->execute();
        $not_submitted_result = $stmt2->get_result();
        $not_submitted = [];
        while ($n_row = $not_submitted_result->fetch_assoc()) {
            $not_submitted[] = $n_row;
        }

        // Output the result
        echo json_encode([
            "submitted" => $submitted,
            "not_submitted" => $not_submitted
        ]);
    } else {
        echo json_encode(["error" => "Assignment not found"]);
    }
} else {
    echo json_encode(["error" => "assignment_title missing"]);
}
?>
