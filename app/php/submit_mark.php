<?php
header("Content-Type: application/json");

try {
    $pdo = new PDO("mysql:host=localhost;dbname=school1;charset=utf8mb4", "root", "");
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $data = json_decode(file_get_contents("php://input"), true);

    if (!isset($data['student_id'], $data['user_id'], $data['class_id'], $data['mark'], $data['type'])) {
        echo json_encode(["error" => "Missing required fields"]);
        exit;
    }

    $studentId = $data['student_id'];
    $userId = $data['user_id']; 
    $classId = $data['class_id'];
    $mark = floatval($data['mark']);
    $type = strtolower(trim($data['type']));

    $teacherStmt = $pdo->prepare("SELECT id FROM teacher WHERE user_id = :user_id");
    $teacherStmt->execute(['user_id' => $userId]);
    $teacherRow = $teacherStmt->fetch(PDO::FETCH_ASSOC);

    if (!$teacherRow) {
        echo json_encode(["error" => "No teacher found for this user"]);
        exit;
    }

    $teacherId = $teacherRow['id'];

    $stmt = $pdo->prepare("SELECT subject_id FROM schedule WHERE teacher_id = :teacher_id AND class_id = :class_id LIMIT 1");
    $stmt->execute(['teacher_id' => $teacherId, 'class_id' => $classId]);
    $row = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$row) {
        echo json_encode(["error" => "No subject found for this class and teacher"]);
        exit;
    }

    $subjectId = $row['subject_id'];

    if ($type === "assignment") {
        $stmt = $pdo->prepare("
            INSERT INTO marks_schedule (student_id, subject_id, assignment_mark)
            VALUES (:student_id, :subject_id, :mark)
            ON DUPLICATE KEY UPDATE assignment_mark = :mark
        ");
        $stmt->execute([
            'student_id' => $studentId,
            'subject_id' => $subjectId,
            'mark' => $mark
        ]);
    } elseif ($type === "mid exam" || $type === "final exam") {
        $examType = ($type === "mid exam") ? "mid" : "final";

        $examStmt = $pdo->prepare("
            SELECT e.id
            FROM exam e
            JOIN class_exam ce ON ce.exam_id = e.id
            WHERE ce.class_id = :class_id AND ce.teacher_id = :teacher_id AND e.type = :type
            LIMIT 1
        ");
        $examStmt->execute([
            'class_id' => $classId,
            'teacher_id' => $teacherId,
            'type' => $examType
        ]);
        $exam = $examStmt->fetch(PDO::FETCH_ASSOC);

        if (!$exam) {
            echo json_encode(["error" => "No exam found for this class and type"]);
            exit;
        }

        $examId = $exam['id'];

        $stmt = $pdo->prepare("
            INSERT INTO exam_mark (exam_id, student_id, mark)
            VALUES (:exam_id, :student_id, :mark)
            ON DUPLICATE KEY UPDATE mark = :mark
        ");
        $stmt->execute([
            'exam_id' => $examId,
            'student_id' => $studentId,
            'mark' => $mark
        ]);
    } else {
        echo json_encode(["error" => "Invalid mark type"]);
        exit;
    }

    echo json_encode(["success" => true]);

} catch (PDOException $e) {
    echo json_encode(["error" => $e->getMessage()]);
}
