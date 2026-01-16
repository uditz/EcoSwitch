package com.example.ecoswitch.userFragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.ecoswitch.R;
import com.example.ecoswitch.userFiles.Request;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.tensorflow.lite.Interpreter;

public class historyfragment extends Fragment {
    // UI Components
    private ImageView imageView;
    private Button submitButton,classifiedBtn;  // Added camera button
    private TextView resultTextView;

    // Bitmap and URI
    private Bitmap receivedBitmap;
    private Uri selectedImageUri;

    // Map Components
    private MapView mapView;
    private MapboxMap mapboxMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // ML Model
    private Interpreter tflite;
    private static final String TAG = "HistoryFragment";  // Tag for logging

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadMLModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historyfragment, container, false);

        initializeUI(view);
        initializeMap(savedInstanceState);  // Pass savedInstanceState here
        setupButtonClickListeners();

        // Request location when fragment is created
        requestUserLocation();

        return view;
    }

    // ====================== UI Functions ======================
    private void initializeUI(View view) {
        resultTextView = view.findViewById(R.id.resultTextView);
        imageView = view.findViewById(R.id.droppedimg);
        submitButton = view.findViewById(R.id.submitImageBtn);
        classifiedBtn = view.findViewById(R.id.classifierBtn);

        // Try to find camera button, add it programmatically if not in layout
//        cameraButton = view.findViewById(R.id.cameraBtn);
//        if (cameraButton == null) {
//            Log.d(TAG, "Camera button not found in layout, consider adding it to your XML");
//            // You could add it programmatically here if needed
//        }

        mapView = view.findViewById(R.id.map);

        if (getArguments() != null) {
            receivedBitmap = getArguments().getParcelable("capturedImage");
            Log.d(TAG, "Received bitmap from arguments: " + (receivedBitmap != null ? "yes" : "no"));
            if (receivedBitmap != null) {
                imageView.setImageBitmap(receivedBitmap);
            }
        }

        // Set a long click listener on the image for additional options
        imageView.setOnLongClickListener(v -> {
            Toast.makeText(requireContext(), "Select source: Gallery or Camera", Toast.LENGTH_SHORT).show();
            showImageSourceOptions();
            return true;
        });

        // Set regular click listener for gallery selection
        imageView.setOnClickListener(v -> selectImageFromGallery());
    }

    private void showImageSourceOptions() {
        // This could be implemented with a dialog, but for simplicity
        // we'll just show camera and gallery options temporarily
        Toast.makeText(requireContext(), "Long press: Camera | Single tap: Gallery", Toast.LENGTH_LONG).show();

        // Trigger camera after short delay
        imageView.postDelayed(this::captureImageFromCamera, 2000);
    }

    private void setupButtonClickListeners() {
        submitButton.setOnClickListener(v -> {
            if (receivedBitmap == null && selectedImageUri == null) {
                Toast.makeText(requireContext(), "No image selected or captured!", Toast.LENGTH_SHORT).show();
            } else {
                Bitmap bitmapToClassify = receivedBitmap != null ? receivedBitmap : getBitmapFromUri(selectedImageUri);
                if (bitmapToClassify != null) {

                    // Upload the image
                    if (selectedImageUri != null) {
                        uploadImageToCloudinary(selectedImageUri);
                    } else if (receivedBitmap != null) {
                        Uri imageUri = getImageUri(requireContext(), receivedBitmap);
                        uploadImageToCloudinary(imageUri);
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        classifiedBtn.setOnClickListener(v -> {
            if (receivedBitmap == null && selectedImageUri == null) {
                Toast.makeText(requireContext(), "No image selected or captured!", Toast.LENGTH_SHORT).show();
            } else {
                Bitmap bitmapToClassify = receivedBitmap != null ? receivedBitmap : getBitmapFromUri(selectedImageUri);
                if (bitmapToClassify != null) {
                    classifyImage(bitmapToClassify);  // Classify the image
                } else {
                    Toast.makeText(requireContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // Set up camera button if it exists
//        if (cameraButton != null) {
//            cameraButton.setOnClickListener(v -> captureImageFromCamera());
//        }
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    imageView.setImageURI(selectedImageUri);
                    // Clear receivedBitmap as we're now using gallery image
                    receivedBitmap = null;
                    Log.d(TAG, "Image selected from gallery");
                }
            });

    private void captureImageFromCamera() {
        Log.d(TAG, "Starting camera capture");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == requireActivity().RESULT_OK && result.getData() != null) {
                    Log.d(TAG, "Camera result received");
                    Bundle extras = result.getData().getExtras();
                    receivedBitmap = (Bitmap) extras.get("data");
                    // Clear selected URI as we're now using camera image
                    selectedImageUri = null;

                    if (receivedBitmap != null) {
                        Log.d(TAG, "Setting camera image to ImageView");
                        imageView.setImageBitmap(receivedBitmap);
                        // Optionally auto-classify
                        // classifyImage(receivedBitmap);
                    } else {
                        Log.e(TAG, "Camera returned null bitmap");
                    }
                } else {
                    Log.d(TAG, "Camera capture cancelled or failed");
                }
            });

    // ====================== Map Functions ======================
    private void initializeMap(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        String key = "ufNVdE4llmTEahct6tex";
        String mapId = "streets-v2";
        String styleUrl = "https://api.maptiler.com/maps/" + mapId + "/style.json?key=" + key;

        mapView.getMapAsync(map -> {
            mapboxMap = map;
            map.setStyle(new Style.Builder().fromUri(styleUrl), style -> {
                enableUserLocation(style); // Enable location component
                zoomToCurrentLocation();   // Zoom to the user's location
            });
        });
    }
    private void zoomToCurrentLocation() {
        if (mapboxMap == null) return;

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                        mapboxMap.setCameraPosition(
                                new CameraPosition.Builder()
                                        .target(userLocation)
                                        .zoom(15.0) // Adjust zoom level as needed
                                        .build()
                        );

                        Log.d(TAG, "Zoomed to user location: " + userLocation);
                    } else {
                        Log.d(TAG, "Last known location is null");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get last location", e);
                });
    }

    private void enableUserLocation(Style style) {
        if (mapboxMap == null || style == null) return;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(requireContext(), style).build()
            );
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void requestUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000)
                    .setFastestInterval(2000);

            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult != null && locationResult.getLastLocation() != null) {
                        Location location = locationResult.getLastLocation();
                        updateMapLocation(location);
                        fusedLocationClient.removeLocationUpdates(this);
                    }
                }
            }, requireActivity().getMainLooper());

        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Location request failed! Please grant permissions.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMapLocation(Location location) {
        if (mapboxMap != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

            mapboxMap.setCameraPosition(new CameraPosition.Builder()
                    .target(userLocation)
                    .zoom(15.0)
                    .build());

            Toast.makeText(requireContext(), "Location: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        }
    }

    // ====================== ML Model Functions ======================
    private void loadMLModel() {
        try {
            // List all assets for debugging
            try {
                String[] assets = requireContext().getAssets().list("");
                Log.d(TAG, "Assets directory content: " + Arrays.toString(assets));
            } catch (IOException e) {
                Log.e(TAG, "Failed to list assets", e);
            }

            tflite = new Interpreter(loadModelFile());
            Log.d(TAG, "Model loaded successfully");
            Toast.makeText(requireContext(), "Model loaded", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Model loading failed: " + e.getMessage());
            Toast.makeText(requireContext(), "Failed to load model: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        try {
            AssetFileDescriptor fileDescriptor = requireContext().getAssets().openFd("compatible_model.tflite");
            FileInputStream fileInputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = fileInputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            Log.e(TAG, "Error loading model file", e);
            throw e;
        }
    }

    private void classifyImage(Bitmap bitmap) {
        try {
            resultTextView.setText("Processing image...");
            Log.d(TAG, "Starting image classification");

            float[][][][] input = preprocessImage(bitmap);
            float[][] output = new float[1][5]; // Assuming 5 output classes

            if (tflite == null) {
                Log.e(TAG, "TensorFlow model not loaded!");
                Toast.makeText(requireContext(), "TensorFlow model not loaded!", Toast.LENGTH_SHORT).show();
                return;
            }

            tflite.run(input, output);

            // Log raw model output for debugging
            StringBuilder logOutput = new StringBuilder("Raw model output: ");
            for (float val : output[0]) {
                logOutput.append(val).append(", ");
            }
            Log.d(TAG, logOutput.toString());

            int predictedClass = getMaxIndex(output[0]);
            String[] classLabels = {"Battery", "Keyboard", "Mobile", "Mouse", "Television"};
            String classification = classLabels[predictedClass];

            Log.d(TAG, "Classification complete: " + classification);
            resultTextView.setText("Predicted: " + classification);
            Toast.makeText(requireContext(), "Classification: " + classification, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Classification failed", e);
            resultTextView.setText("Classification failed: " + e.getMessage());
            Toast.makeText(requireContext(), "Image classification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private float[][][][] preprocessImage(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        float[][][][] input = new float[1][224][224][3];

        for (int y = 0; y < 224; y++) {
            for (int x = 0; x < 224; x++) {
                int pixel = resizedBitmap.getPixel(x, y);
                input[0][y][x][0] = ((pixel >> 16) & 0xFF) / 255.0f; // Red
                input[0][y][x][1] = ((pixel >> 8) & 0xFF) / 255.0f;  // Green
                input[0][y][x][2] = (pixel & 0xFF) / 255.0f;         // Blue
            }
        }

        return input;
    }

    private int getMaxIndex(float[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    // ====================== Cloud Functions ======================
//    private void uploadImageToCloudinary(Uri imageUri) {
//        try {
//            Log.d(TAG, "Starting Cloudinary upload");
//            File imageFile = uriToFile(imageUri);
//            if (imageFile == null) {
//                Toast.makeText(requireContext(), "Failed to convert image", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            Toast.makeText(requireContext(), "Uploading image...", Toast.LENGTH_SHORT).show();
//
//            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
//                    "cloud_name", "dyezx2jyp",
//                    "api_key", "248357263686488",
//                    "api_secret", "zqVZfCdjadJbmeT0pTYrj9pDQCQ"
//            ));
//
//            new Thread(() -> {
//                try {
//                    Map uploadResult = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap());
//                    if (uploadResult.containsKey("secure_url")) {
//                        String imageUrl = (String) uploadResult.get("secure_url");
//                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                        Log.d(TAG, "Upload successful, URL: " + imageUrl);
//                        requireActivity().runOnUiThread(() -> {
//                            Toast.makeText(requireContext(), "Upload successful!", Toast.LENGTH_SHORT).show();
//                            saveImageLocationToFirestore(imageUrl, currentUserId, resultTextView.getText().toString());
//                        });
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Log.e(TAG, "Cloudinary upload failed", e);
//                    requireActivity().runOnUiThread(() ->
//                            Toast.makeText(requireContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
//                    );
//                }
//            }).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e(TAG, "Cloudinary preparation failed", e);
//            Toast.makeText(requireContext(), "Cloudinary upload failed!", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void uploadImageToCloudinary(Uri imageUri) {
        try {
            Log.d(TAG, "Starting Cloudinary upload");
            File imageFile = uriToFile(imageUri);
            if (imageFile == null) {
                Toast.makeText(requireContext(), "Failed to convert image", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(requireContext(), "Uploading image...", Toast.LENGTH_SHORT).show();

            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "dyezx2jyp",
                    "api_key", "248357263686488",
                    "api_secret", "zqVZfCdjadJbmeT0pTYrj9pDQCQ"
            ));

            new Thread(() -> {
                try {
                    Map uploadResult = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap());
                    if (uploadResult.containsKey("secure_url")) {
                        String imageUrl = (String) uploadResult.get("secure_url");
                        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d(TAG, "Upload successful, URL: " + imageUrl);

                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Upload successful!", Toast.LENGTH_SHORT).show();
                            getUserLocationAndSave(imageUrl, currentUserId);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Cloudinary upload failed", e);
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Cloudinary preparation failed", e);
            Toast.makeText(requireContext(), "Cloudinary upload failed!", Toast.LENGTH_SHORT).show();
        }
    }
    private void getUserLocationAndSave(String imageUrl, String userId) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Location permissions required!", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Log.d(TAG, "User location: Lat=" + latitude + ", Lng=" + longitude);
                // Call Firestore save function with location
                saveImageLocationToFirestore(imageUrl, userId, resultTextView.getText().toString(), latitude, longitude);
            } else {
                Toast.makeText(requireContext(), "Failed to get location!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get user location", e);
            Toast.makeText(requireContext(), "Location retrieval failed!", Toast.LENGTH_SHORT).show();
        });
    }
        private void saveImageLocationToFirestore(String imageUrl, String userId, String classification, double latitude, double longitude) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d(TAG, "Saving to Firestore: " + classification);
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String location = documentSnapshot.getString("location"); // Default location from Firestore
                            // Use Geocoder to find city, state, country
                            String detectedLocation = getCityFromCoordinates(latitude, longitude);
                            if (detectedLocation != null) {
                                location = detectedLocation; // If Geocoder succeeds, update location
                            }
                            DocumentReference requestRef = db.collection("requests").document();
                            String requestId = requestRef.getId();
                            // Extract classification name if it contains extra text
                            String extractedClassification = classification;
                            if (classification.startsWith("Predicted: ")) {
                                extractedClassification = classification.substring("Predicted: ".length());
                            }
                            // Get current date & time
                            Timestamp timestamp = Timestamp.now();
                            String status="pending";
                            // Create Request object
                            Request request = new Request(requestId, userId, name, location, imageUrl, extractedClassification, timestamp,status);
                            requestRef.set(request)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Request stored successfully");
                                        Toast.makeText(requireContext(), "Request stored with classification!", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Failed to store request", e);
                                        Toast.makeText(requireContext(), "Failed to store request!", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Log.d(TAG, "User document does not exist");
                            Toast.makeText(requireContext(), "User information not found!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to retrieve user data", e);
                        Toast.makeText(requireContext(), "Failed to retrieve user data!", Toast.LENGTH_SHORT).show();
                    });
        }
    /**
     * Get nearest city from latitude and longitude using Geocoder.
     */
    private String getCityFromCoordinates(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String city = address.getLocality(); // City
                String state = address.getAdminArea(); // State
                String country = address.getCountryName(); // Country

                if (city != null && state != null && country != null) {
                    return city + ", " + state + ", " + country;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Geocoder failed to fetch location");
        }
        return null; // Return null if Geocoder fails
    }
    // ====================== Utility Functions ======================
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to get bitmap from URI", e);
            return null;
        }
    }
//    private Uri getImageUri(Context context, Bitmap bitmap) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "CapturedImage", null);
//        return Uri.parse(path);
//    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            Log.e("historyfragment", "getImageUri: Received null bitmap!");
            return null;
        }

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "CapturedImage", null);

        if (path == null) {
            Log.e("historyfragment", "getImageUri: MediaStore failed to insert image!");
            return null; // Avoid passing null to Uri.parse()
        }

        return Uri.parse(path);
    }

    private File uriToFile(Uri uri) {
        try {
            File file = new File(requireContext().getCacheDir(), "upload_image.jpg");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to convert URI to file", e);
            return null;
        }
    }

    // ====================== Fragment Lifecycle ======================
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        mapView = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mapboxMap != null && mapboxMap.getStyle() != null) {
                    enableUserLocation(mapboxMap.getStyle());
                    requestUserLocation();
                }
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}