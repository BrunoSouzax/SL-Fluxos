package com.souzalima.fluxodeencomendas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CadastroActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private DatabaseReference database;
    private EditText editTextUnit, editTextName, editTextDate, editTextTime, editTextDescription, editTextNotaFiscal;
    private Spinner spinnerPlantonista;
    private EditText editTextOutroPlantonista;
    private String currentPhotoPath;
    private ImageView imageView;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Corrija o layout se necessário

        // Inicializar a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializar o DrawerLayout e o ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.syncState();

        // Verificar se o ActionBar não é nulo
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        // Inicializar elementos da interface do usuário
        editTextUnit = findViewById(R.id.editTextUnit);
        editTextName = findViewById(R.id.editTextName);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextNotaFiscal = findViewById(R.id.editTextNF); // Campo de nota fiscal
        spinnerPlantonista = findViewById(R.id.spinnerPlantonista);
        editTextOutroPlantonista = findViewById(R.id.editTextOutroPlantonista);
        Button buttonTakePhoto = findViewById(R.id.buttonTakePhoto);
        Button buttonAddOrder = findViewById(R.id.buttonAddOrder);
        Button buttonClear = findViewById(R.id.buttonClear);
        imageView = findViewById(R.id.imageViewPhoto);

        // Ação de limpar campos
        buttonClear.setOnClickListener(v -> clearFields());

        // Inicializar referência do Firebase
        database = FirebaseDatabase.getInstance().getReference();

        // Configurar o Spinner de Plantonistas
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.plantonista_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlantonista.setAdapter(adapter);
        spinnerPlantonista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                if (selectedOption.equals("Outro")) {
                    editTextOutroPlantonista.setVisibility(View.VISIBLE);
                } else {
                    editTextOutroPlantonista.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Não é necessário fazer nada aqui
            }
        });

        // Configurar o clique do botão para tirar uma foto
        buttonTakePhoto.setOnClickListener(v -> dispatchTakePictureIntent());

        // Configurar o clique do botão para adicionar uma nova encomenda
        buttonAddOrder.setOnClickListener(v -> addNewOrder());

        // Preencher data e hora atuais automaticamente
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());
        editTextDate.setText(currentDate);
        editTextTime.setText(currentTime);

        // Verificar e solicitar permissões necessárias
        checkPermissions();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Apenas volta à página anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    // Lógica para adicionar uma nova encomenda
    private void addNewOrder() {
        String unit = editTextUnit.getText().toString();
        String name = editTextName.getText().toString();
        String date = editTextDate.getText().toString();
        String time = editTextTime.getText().toString();
        String description = editTextDescription.getText().toString();
        String notaFiscal = editTextNotaFiscal.getText().toString();
        String plantonista;

        if (editTextOutroPlantonista.getVisibility() == View.VISIBLE) {
            plantonista = editTextOutroPlantonista.getText().toString();
        } else {
            plantonista = spinnerPlantonista.getSelectedItem().toString();
        }

        // Verificar se todos os campos foram preenchidos
        if (!unit.isEmpty() && !name.isEmpty() && !date.isEmpty() && !time.isEmpty() && !description.isEmpty() && !notaFiscal.isEmpty() && !plantonista.isEmpty()) {
            String orderId = database.child("orders").push().getKey();
            Order order = new Order(unit, name, date, time, description, plantonista, currentPhotoPath, notaFiscal, orderId);

            // Fazer o upload da imagem para o Firebase Storage
            if (currentPhotoPath != null) {
                uploadImageToFirebaseStorage(currentPhotoPath, order, orderId);
            } else {
                database.child("orders").child(orderId).setValue(order);
                Toast.makeText(CadastroActivity.this, "Encomenda adicionada com sucesso", Toast.LENGTH_SHORT).show();
            }

            // Enviar mensagem pelo WhatsApp
            sendWhatsAppMessage(unit, name, date, time, description, notaFiscal, plantonista, currentPhotoPath);
        } else {
            Toast.makeText(CadastroActivity.this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
    }

    // Função que solicita as permissões necessárias
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        }
    }

    // Recebe o resultado da solicitação de permissões
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
                Toast.makeText(this, "Permissões concedidas", Toast.LENGTH_SHORT).show();
            } else {
                // Permissão negada
                Toast.makeText(this, "Permissões necessárias não concedidas", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para tirar a foto
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("CadastroActivity", "Erro ao criar o arquivo de imagem", ex);
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.souzalima.fluxodeencomendas.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // Método para criar um arquivo de imagem
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(null);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Método para definir a imagem no ImageView
    private void setPic() {
        if (imageView == null || currentPhotoPath == null) {
            Log.e("CadastroActivity", "Erro: imageView ou currentPhotoPath estão nulos.");
            return;
        }

        // Obtém as dimensões da View para exibir a imagem
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Verifica se a ImageView já foi layoutada corretamente
        if (targetW == 0 || targetH == 0) {
            Log.e("CadastroActivity", "Erro: imageView ainda não foi layoutada. Adie a execução.");
            return;
        }

        // Obtém as dimensões do Bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Calcula o fator de escala
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decodifica o arquivo de imagem em um Bitmap redimensionado
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    // Recebe o resultado da atividade de tirar foto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    // Enviar mensagem pelo WhatsApp
    private void sendWhatsAppMessage(String unit, String name, String date, String time, String description, String notaFiscal, String plantonista, String imagePath) {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");

        String message = "Nova encomenda registrada:\n" +
                "Unidade: " + unit + "\n" +
                "Nome: " + name + "\n" +
                "Data: " + date + "\n" +
                "Hora: " + time + "\n" +
                "Descrição: " + description + "\n" +
                "Nota Fiscal: " + notaFiscal + "\n" +
                "Plantonista: " + plantonista;

        whatsappIntent.putExtra(Intent.EXTRA_TEXT, message);

        // Verificar se há uma imagem para anexar
        if (imagePath != null) {
            File imageFile = new File(imagePath);
            Uri imageUri = FileProvider.getUriForFile(this,
                    "com.souzalima.fluxodeencomendas.fileprovider",
                    imageFile);
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            whatsappIntent.setType("image/jpeg");
            whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(CadastroActivity.this, "O WhatsApp não está instalado", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para fazer upload da imagem no Firebase Storage
    private void uploadImageToFirebaseStorage(String imagePath, Order order, String orderId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child("images/" + orderId + ".jpg");

        Uri fileUri = Uri.fromFile(new File(imagePath));
        imagesRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        order.setImageUrl(uri.toString());
                        database.child("orders").child(orderId).setValue(order);
                        Toast.makeText(CadastroActivity.this, "Encomenda adicionada com sucesso", Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(exception -> {
                    Log.e("CadastroActivity", "Falha ao fazer upload da imagem", exception);
                });
    }

    // Limpar campos
    private void clearFields() {
        editTextUnit.setText("");
        editTextName.setText("");
        editTextDate.setText("");
        editTextTime.setText("");
        editTextDescription.setText("");
        editTextNotaFiscal.setText("");
        spinnerPlantonista.setSelection(0);
        editTextOutroPlantonista.setText("");
        imageView.setImageDrawable(null);
        currentPhotoPath = null;
    }

    // Classe para encomenda
    public static class Order {
        public String unit;
        public String name;
        public String date;
        public String time;
        public String description;
        public String plantonista;
        public String imageUrl;
        public String notaFiscal;
        public String orderId;

        public Order() {
            // Construtor vazio necessário para o Firebase
        }

        public Order(String unit, String name, String date, String time, String description, String plantonista, String imageUrl, String notaFiscal, String orderId) {
            this.unit = unit;
            this.name = name;
            this.date = date;
            this.time = time;
            this.description = description;
            this.plantonista = plantonista;
            this.imageUrl = imageUrl;
            this.notaFiscal = notaFiscal;
            this.orderId = orderId;
        }

        // Getters e setters omitidos por brevidade
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
