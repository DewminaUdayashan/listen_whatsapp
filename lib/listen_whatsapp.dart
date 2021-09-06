import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class ListenWhatsapp {
  static const MethodChannel _channel = const MethodChannel('listen_whatsapp');

  static Future<void> startService() async {
    await _channel.invokeMethod('startService');
  }

  static Future<List<Map<String, dynamic>>> getSenders() async {
    List<Map<String, dynamic>> send =
        List<Map<String, dynamic>>.empty(growable: true);
    final list = await _channel.invokeListMethod("getSenders");
    for (var value in list!) {
      send.add(Map<String, dynamic>.from(value));
    }
    return send;
  }

  static Future<List<Map<String, dynamic>>> getMessages() async {
    List<Map<String, dynamic>> send =
        List<Map<String, dynamic>>.empty(growable: true);
    final list = await _channel.invokeListMethod("getMessages");
    for (var value in list!) {
      send.add(Map<String, dynamic>.from(value));
    }
    return send;
  }

  static Future<List<Map<String, dynamic>>> getGroups() async {
    List<Map<String, dynamic>> send =
        List<Map<String, dynamic>>.empty(growable: true);
    final list = await _channel.invokeListMethod("getGroups");
    for (var value in list!) {
      send.add(Map<String, dynamic>.from(value));
    }
    return send;
  }

  static Future<List<Map<String, dynamic>>> getGroupMessages() async {
    List<Map<String, dynamic>> send =
        List<Map<String, dynamic>>.empty(growable: true);
    final list = await _channel.invokeListMethod("getGroupMessages");
    for (var value in list!) {
      send.add(Map<String, dynamic>.from(value));
    }
    return send;
  }

  static Future<bool> checkIsServiceEnabled() async {
    final bool val = await _channel.invokeMethod("checkNotificationService");
    return val;
  }
}
