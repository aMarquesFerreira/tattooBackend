package tattoowebsite.tattobackend.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tattoowebsite.tattobackend.model.Comment;
import tattoowebsite.tattobackend.model.AcceptedComment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    private static final String SPREADSHEET_ID = "1n2d8c77YZamxIcXu_7AwS5EyzVk-U8Gm7Rj8b57wgSs";
    private static final String COMMENTS_RANGE = "Comments!A:D";
    private static final String ACCEPTED_COMMENTS_RANGE = "AcceptedComments!A:D";

    @Autowired
    private Sheets sheetsService;

    public List<Comment> readComments() throws IOException {
        List<Comment> comments = new ArrayList<>();
        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, COMMENTS_RANGE).execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List<Object> row : values) {
                Comment comment = new Comment();
                comment.setId(Long.parseLong(row.get(0).toString()));
                comment.setName(row.get(1).toString());
                comment.setRating(Integer.parseInt(row.get(2).toString()));
                comment.setDescription(row.get(3).toString());
                comments.add(comment);
            }
        }
        return comments;
    }

    public List<AcceptedComment> readAcceptedComments() throws IOException {
        List<AcceptedComment> comments = new ArrayList<>();
        ValueRange response = sheetsService.spreadsheets().values().get(SPREADSHEET_ID, ACCEPTED_COMMENTS_RANGE).execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        } else {
            for (List<Object> row : values) {
                AcceptedComment comment = new AcceptedComment();
                comment.setId(Long.parseLong(row.get(0).toString()));
                comment.setName(row.get(1).toString());
                comment.setRating(Integer.parseInt(row.get(2).toString()));
                comment.setDescription(row.get(3).toString());
                comments.add(comment);
            }
        }
        return comments;
    }

    public void writeComment(Comment comment) throws IOException {
        if (comment == null) {
            throw new NullPointerException("O objeto Comment é nulo");
        }
        if (comment.getId() == null) {
            throw new NullPointerException("O campo ID do Comment é nulo");
        }
        if (comment.getName() == null) {
            throw new NullPointerException("O campo Name do Comment é nulo");
        }
        if (comment.getRating() == null) {
            throw new NullPointerException("O campo Rating do Comment é nulo");
        }
        if (comment.getDescription() == null) {
            throw new NullPointerException("O campo Description do Comment é nulo");
        }

        // Verificar e criar a aba "Comments" se não existir
        createSheetIfNotExists("Comments");

        List<List<Object>> values = List.of(
                List.of(comment.getId(), comment.getName(), comment.getRating(), comment.getDescription())
        );
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .append(SPREADSHEET_ID, COMMENTS_RANGE, body)
                .setValueInputOption("RAW")
                .execute();
    }

    public void writeAcceptedComment(AcceptedComment comment) throws IOException {
        if (comment == null) {
            throw new NullPointerException("O objeto AcceptedComment é nulo");
        }
        if (comment.getId() == null) {
            throw new NullPointerException("O campo ID do AcceptedComment é nulo");
        }
        if (comment.getName() == null) {
            throw new NullPointerException("O campo Name do AcceptedComment é nulo");
        }
        if (comment.getRating() == null) {
            throw new NullPointerException("O campo Rating do AcceptedComment é nulo");
        }
        if (comment.getDescription() == null) {
            throw new NullPointerException("O campo Description do AcceptedComment é nulo");
        }

        // Verificar e criar a aba "AcceptedComments" se não existir
        createSheetIfNotExists("AcceptedComments");

        List<List<Object>> values = List.of(
                List.of(comment.getId(), comment.getName(), comment.getRating(), comment.getDescription())
        );
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .append(SPREADSHEET_ID, ACCEPTED_COMMENTS_RANGE, body)
                .setValueInputOption("RAW")
                .execute();
    }

public void deleteComment(Long id) throws IOException {
    System.out.println("Deleting comment with ID: " + id);

    // Obter a planilha para verificar o ID da aba
    Spreadsheet spreadsheet = sheetsService.spreadsheets().get(SPREADSHEET_ID).execute();
    Sheet sheet = spreadsheet.getSheets().stream()
            .filter(s -> s.getProperties().getTitle().equals("Comments"))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Aba 'Comments' não encontrada"));

    int sheetId = sheet.getProperties().getSheetId();
    System.out.println("Sheet ID for 'Comments': " + sheetId);

    List<List<Object>> values = sheetsService.spreadsheets().values()
            .get(SPREADSHEET_ID, COMMENTS_RANGE).execute().getValues();
    int rowIndex = -1;
    if (values != null) {
        for (int i = 0; i < values.size(); i++) {
            System.out.println("Checking row: " + i + " with value: " + values.get(i).get(0));
            if (values.get(i).get(0).toString().equals(id.toString())) {
                rowIndex = i;
                System.out.println("Found row index: " + rowIndex);
                break;
            }
        }
    }

    if (rowIndex != -1) {
        List<Request> requests = new ArrayList<>();
        requests.add(new Request()
                .setDeleteDimension(new DeleteDimensionRequest()
                        .setRange(new DimensionRange()
                                .setSheetId(sheetId) // Usando o ID da aba dinâmico
                                .setDimension("ROWS")
                                .setStartIndex(rowIndex)
                                .setEndIndex(rowIndex + 1))));
        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        BatchUpdateSpreadsheetResponse response = sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID, body).execute();
        
        // Adicionar logs detalhados da resposta
        System.out.println("Batch update response: " + response.toString());

        // Verificar se a linha realmente foi removida
        values = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, COMMENTS_RANGE).execute().getValues();
        if (values != null) {
            boolean rowStillExists = values.stream()
                    .anyMatch(row -> row.get(0).toString().equals(id.toString()));
            if (rowStillExists) {
                System.out.println("Failed to delete row with ID: " + id);
            } else {
                System.out.println("Successfully deleted row with ID: " + id);
            }
        } else {
            System.out.println("Failed to retrieve values after deletion to confirm removal.");
        }
    } else {
        System.out.println("Row with ID " + id + " not found.");
    }
}



    public void deleteAcceptedComment(Long id) throws IOException {
        // Verificar e criar a aba "AcceptedComments" se não existir
        createSheetIfNotExists("AcceptedComments");

        List<List<Object>> values = sheetsService.spreadsheets().values()
                .get(SPREADSHEET_ID, ACCEPTED_COMMENTS_RANGE).execute().getValues();
        int rowIndex = -1;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).get(0).equals(id.toString())) {
                rowIndex = i;
                break;
            }
        }
        if (rowIndex != -1) {
            List<Request> requests = new ArrayList<>();
            requests.add(new Request()
                    .setDeleteDimension(new DeleteDimensionRequest()
                            .setRange(new DimensionRange()
                                    .setSheetId(0)
                                    .setDimension("ROWS")
                                    .setStartIndex(rowIndex)
                                    .setEndIndex(rowIndex + 1))));
            BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
            sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID, body).execute();
        }
    }

    private void createSheetIfNotExists(String sheetName) throws IOException {
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(SPREADSHEET_ID).execute();
        boolean sheetExists = spreadsheet.getSheets().stream()
                .anyMatch(sheet -> sheet.getProperties().getTitle().equals(sheetName));

        if (!sheetExists) {
            SheetProperties sheetProperties = new SheetProperties();
            sheetProperties.setTitle(sheetName);

            AddSheetRequest addSheetRequest = new AddSheetRequest();
            addSheetRequest.setProperties(sheetProperties);

            Request request = new Request();
            request.setAddSheet(addSheetRequest);

            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest();
            batchUpdateRequest.setRequests(List.of(request));

            sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID, batchUpdateRequest).execute();
        } else {
            int sheetId = spreadsheet.getSheets().stream()
                .filter(sheet -> sheet.getProperties().getTitle().equals(sheetName))
                .findFirst()
                .get()
                .getProperties()
                .getSheetId();
            System.out.println(sheetName + " sheet ID is: " + sheetId);
        }
    }


    public void acceptComment(Long commentId) throws IOException {
        // Read all comments
        List<Comment> comments = readComments();

        // Find the comment with the given ID
        Comment commentToAccept = comments.stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Comentário não encontrado: " + commentId));

        // Convert to AcceptedComment
        AcceptedComment acceptedComment = new AcceptedComment();
        acceptedComment.setId(commentToAccept.getId());
        acceptedComment.setName(commentToAccept.getName());
        acceptedComment.setRating(commentToAccept.getRating());
        acceptedComment.setDescription(commentToAccept.getDescription());

        // Delete from Comments sheet
        deleteComment(commentId);
        // Write to AcceptedComments sheet
        writeAcceptedComment(acceptedComment);

    }
}
