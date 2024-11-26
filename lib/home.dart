import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Home extends StatelessWidget {
  const Home({super.key});

  static const platform = MethodChannel('notification');

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
          child: ElevatedButton(
              onPressed: () async {
                await platform.invokeMethod('send_notification', {
                  "title": "This is title from flutter",
                  "description": "this description from flutter",
                });
              },
              child: const Text('send notification'))),
    );
  }
}
