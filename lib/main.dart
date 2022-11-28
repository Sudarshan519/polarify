import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;
  var isNFCAvailable;
  var data;
  static const _ekycPlatform = MethodChannel('com.nitv.bnpjcredit/ekyc');
  static const _nfcPlatform = MethodChannel('com.nitv.bnpjcredit/nfc');
  void _incrementCounter() {
    _ekycPage();
    setState(() {
      _counter++;
    });
  }

  _ekycPage() async {
    // var isnfcAvailable =
    //     await _nfcPlatform.invokeMethod("isNFCAvailable") as bool;
    // isNFCAvailable = isnfcAvailable;
    // setState(() {});
    // if (isNFCAvailable) {
    openScanner();
    // }
    // print(isNFCAvailable);
  }

  openScanner() async {
    var documentType = "RESIDENCE_CARD";
    var isManualEKYC = true;
    final params = {
      "document_type": documentType,
      // "isManualEKYC": isManualEKYC ? '1' : '0',
      "document_number": "2123123123",
      "pin1": "1234",
      "pin2": "1234"
    };
    var nfcResult =
        await _ekycPlatform.invokeMethod("openFrontDocumentScanner", params);
    print(nfcResult);
    data = nfcResult;
    setState(() {});
  }

  ///check if nfc available
  checkNfc() async {
    var isnfcAvailable =
        await _nfcPlatform.invokeMethod("isNFCAvailable") as bool;
    return isnfcAvailable;
  }

  ///open nfc reader
  openNfcReader() async {
    var documentType = "RESIDENCE_CARD";
    var isManualEKYC = true;
    final params = {
      "document_type": documentType,
      // "isManualEKYC": isManualEKYC ? '1' : '0',
      "document_number": "2123123123",
      "pin1": "1234",
      "pin2": "1234"
    };
    var nfcData =
        await _nfcPlatform.invokeMethod("openDocumentScanner", params);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
                onPressed: () {
                  openNfcReader();
                },
                child: Text("Open RcReader")),
            // const Text(
            //   'NFC CHECK Result',
            // ),
            Text(data.toString()),
            // Text(
            //   isNFCAvailable.toString(),
            //   style: Theme.of(context).textTheme.headline4,
            // ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),
    );
  }
}
