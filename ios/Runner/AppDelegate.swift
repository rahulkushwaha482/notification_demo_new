import UIKit
import Flutter
import UserNotifications

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {

    let controller : FlutterViewController = window?.rootViewController as! FlutterViewController
    let methodChannel = FlutterMethodChannel(name: "notification",
                                              binaryMessenger: controller.binaryMessenger)
    
        methodChannel.setMethodCallHandler { [weak self] (call: FlutterMethodCall, result: @escaping FlutterResult) in
                 guard call.method == "send_notification" else {
                     result(FlutterMethodNotImplemented)
                     return
                 }
            
            if let args = call.arguments as? [String: String] {
                
                
                self?.scheduleNotification(result: result,arguments: args)
            }
             }
        
        // Set UNUserNotificationCenter delegate
        UNUserNotificationCenter.current().delegate = self
        
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
                if granted {
                    print("Notification authorization granted")
                   
                } else {
                    print("Notification authorization denied")
                }
            }
            
    
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
    
    // Handle notification when the app is in the foreground
    override  func userNotificationCenter(_ center: UNUserNotificationCenter,
                                 willPresent notification: UNNotification,
                                 withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
       completionHandler([.alert, .sound, .badge])
     }
  
  private  func scheduleNotification(result: FlutterResult,arguments: [String: String]) {
      var title   = arguments["title"] ?? "Title Here"
      var description = arguments["description"] ?? "Description body here"
    
           
        
      let content = UNMutableNotificationContent()
          content.title = title
          content.body = description
      content.sound = UNNotificationSound.default

        //  let action1 = UNNotificationAction(identifier: "snoozeAction", title: "Snooze", options: [])
          let action2 = UNNotificationAction(identifier: "cancelAction", title: "Cancel", options: [.destructive])

          let category = UNNotificationCategory(identifier: "meetingCategory", actions: [ action2], intentIdentifiers: [], options: [])

          UNUserNotificationCenter.current().setNotificationCategories([category])
          content.categoryIdentifier = "meetingCategory"

          let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)

          let request = UNNotificationRequest(identifier: "meetingNotification", content: content, trigger: trigger)

          UNUserNotificationCenter.current().add(request) { error in
              if let error = error {
                  print("Error scheduling notification: \(error.localizedDescription)")
              } else {
                  print("Notification with actions scheduled successfully")
              }
          }
    }
    
    
}
