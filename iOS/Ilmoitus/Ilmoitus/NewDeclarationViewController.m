//
//  SecondViewController.m
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 22-04-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "NewDeclarationViewController.h"
#import "NewDeclarationLineViewController.h"
#import "Declaration.h"
#import "DeclarationLine.h"
#import "Supervisor.h"
#import "constants.h"

@interface NewDeclarationViewController ()
@property (weak, nonatomic) IBOutlet UITextField *supervisor;
@property (weak, nonatomic) NSMutableArray *supervisorList;
@property (weak, nonatomic) IBOutlet UITextView *comment;
@property (weak, nonatomic) IBOutlet UITableView *tableView;
@end

@implementation NewDeclarationViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    _supervisor.delegate = self;
    [_comment setReturnKeyType: UIReturnKeyDone];
    _comment.delegate = self;
    
    [self getSupervisorList];
    
    if (_declaration == nil) {
        _declaration = [[Declaration alloc] init];
        _declaration.lines = [[NSMutableArray alloc] init];
    }
    _comment.text = _declaration.comment;
}

- (IBAction)postDeclaration:(id)sender {
    _declaration.className = @"open_declaration";
    _declaration.status = @"Open";
        // TODO get current date in right format
    _declaration.createdAt = @"2014-05-15 07:27:33.448849";
    _declaration.createdBy = [[[NSUserDefaults standardUserDefaults] stringForKey:@"person_id"] longLongValue];
    
    // TODO Get supervisor from dropdown
    NSMutableArray *at = [[NSMutableArray alloc]init];
    [at addObject:[NSNumber numberWithLongLong:[[[NSUserDefaults standardUserDefaults] stringForKey:@"supervisor"] longLongValue]]];
    _declaration.assignedTo = at;
    _declaration.comment = _comment.text;
    // TODO Calc price and items
    _declaration.itemsTotalPrice = 30.00;
    _declaration.itemsCount = 2;
    [self saveDeclaration];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField {
    [textField resignFirstResponder];
    return YES;
}

-(BOOL)textViewShouldEndEditing:(UITextView *)textView {
    _declaration.comment = _comment.text;
    return YES;
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text {
    
    if([text isEqualToString:@"\n"]) {
        [textView resignFirstResponder];
        return NO;
    }
    
    return YES;
}

- (IBAction)cancelDeclaration:(id)sender
{
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)saveDeclaration
{
    
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] stringForKey:@"token"] forHTTPHeaderField:@"Authorization"];
    
    [manager.reachabilityManager startMonitoring];
    
    [manager.reachabilityManager setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status)
     {
         switch (status)
         {
             case AFNetworkReachabilityStatusReachableViaWWAN:
             case AFNetworkReachabilityStatusReachableViaWiFi:
                 NSLog(@"Connected to network");
                 [self uploadDeclaration:manager];
                 break;
             case AFNetworkReachabilityStatusNotReachable:
                 NSLog(@"No internet connection");
                 [self showErrorMessage:@"Geen verbinding" : @"Kon geen verbinding maken met een netwerk"];
                 break;
             default:
                 NSLog(@"Unknown internet connection");
                 [self showErrorMessage:@"Onbekende verbinding" : @"Verbonden met een onbekend soort netwerk"];
                 break;
         }
     }];
}

-(void)uploadDeclaration:(AFHTTPRequestOperationManager*) manager
{
    Declaration *decl = _declaration;
    // Declaration
    NSDictionary *declaration = @{@"state":decl.status, @"created_at":decl.createdAt, @"created_by":[NSNumber numberWithLongLong:decl.createdBy], @"assigned_to":decl.assignedTo, @"comment":decl.comment, @"items_total_price":[NSNumber numberWithFloat:decl.itemsTotalPrice], @"items_count":[NSNumber numberWithInt:decl.itemsCount]};
    
    // Lines
    NSMutableArray *declarationlines = [[NSMutableArray alloc] init];
    for (DeclarationLine *line in decl.lines)
    {
        NSDictionary *currentline = @{@"receipt_date": line.date, @"cost":[NSNumber numberWithFloat:line.cost], @"declaration_sub_type":[NSNumber numberWithLongLong:line.subtype]};
        [declarationlines addObject:currentline];
    }
    
    // TODO Attachments
    
    // Total dict
    NSDictionary *params = @{@"declaration":declaration, @"lines":declarationlines, @"attachments":@""};
    
    NSLog(@"JSON data that is going to be saved/sent: %@",params);
    
    NSString *url = [NSString stringWithFormat:@"%@/declaration", baseURL];
    AFHTTPRequestOperation *apiRequest = [manager POST:url parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject)
    {
        NSError* error;
        NSDictionary* json = [NSJSONSerialization
                              JSONObjectWithData:responseObject
                              
                              options:kNilOptions
                              error:&error];
        
        NSLog(@"JSON response data for saving declaration: %@",json);
        // Handle success
        
    }
    failure:^(AFHTTPRequestOperation *operation, NSError *error)
    {
        switch (operation.response.statusCode)
        {
            case 400:
                NSLog(@"POST request Error 400 for post new declaration: %@", error);
                [self showErrorMessage:@"Verkeerde aanvraag" :operation.responseString];
                break;
                
            case 401:
                NSLog(@"POST request Error 401 for post new declaration: %@", error);
                [self showErrorMessage:@"Onvoldoende rechten" :operation.responseString];
                break;
                
            case 404:
                NSLog(@"POST request Error 404 for post new declaration: %@", error);
                [self showErrorMessage:@"Niet gevonden" :operation.responseString];
                break;
                
            default:
                NSLog(@"POST request Error for post new declaration: %@", error);
                [self showErrorMessage:@"Fout" :operation.responseString];
                break;
        }
    }];
    
    [apiRequest start];
}

-(void)getSupervisorList
{
    // Do Request
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFHTTPRequestSerializer serializer];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] stringForKey:@"token"] forHTTPHeaderField:@"Authorization"];
    [manager.reachabilityManager startMonitoring];
    
    [manager.reachabilityManager setReachabilityStatusChangeBlock:^(AFNetworkReachabilityStatus status)
     {
         switch (status)
         {
             case AFNetworkReachabilityStatusReachableViaWWAN:
             case AFNetworkReachabilityStatusReachableViaWiFi:
                 NSLog(@"Connected to network");
                 [self downloadSupervisorFromServer:manager];
                 break;
             case AFNetworkReachabilityStatusNotReachable:
                 NSLog(@"No internet connection");
                 [self showErrorMessage:@"Geen verbinding" : @"Kon geen verbinding maken met een netwerk"];
                 break;
             default:
                 NSLog(@"Unknown internet connection");
                 [self showErrorMessage:@"Onbekende verbinding" : @"Verbonden met een onbekend soort netwerk"];
                 break;
         }
     }];

    }

- (void) downloadSupervisorFromServer:(AFHTTPRequestOperationManager*) manager
{
    NSString *url = [NSString stringWithFormat:@"%@/supervisors/", baseURL];
    [manager GET:url parameters:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSError* error;
        NSDictionary* json = [NSJSONSerialization
                              JSONObjectWithData:responseObject
                              
                              options:kNilOptions
                              error:&error];
        
        NSLog(@"JSON response: %@", json);
        
        NSMutableArray *supervisorsFound = [[NSMutableArray alloc] init];
        for (NSDictionary *supervisor in json) {
            Supervisor *sup = [[Supervisor alloc] init];
            sup.class_name = supervisor[@"class_name"];
            sup.first_name = supervisor[@"first_name"];
            sup.last_name = supervisor[@"last_name"];
            sup.email = supervisor[@"email"];
            sup.employee_number = [supervisor[@"employee_number"] integerValue];
            sup.department = [supervisor[@"department"] longLongValue];
            sup.supervisor = [supervisor[@"supervisor"] longLongValue];
            sup.max_declaration_price = [supervisor[@"max_declaration_price"] floatValue];
            
            if ([supervisor[@"id"] longLongValue] == [[[NSUserDefaults standardUserDefaults] stringForKey:@"supervisor"] longLongValue]) {
                _supervisor.text = [NSString stringWithFormat:@"%@ %@", sup.first_name, sup.last_name];
            }
            
            [supervisorsFound addObject:sup];
        }
        _supervisorList = supervisorsFound;
        
        // TODO create dropdown to select supervisor
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        switch (operation.response.statusCode)
        {
            case 400:
                NSLog(@"GET request Error 400 for get supervisors: %@", error);
                [self showErrorMessage:@"Verkeerde aanvraag" :operation.responseString];
                break;
                
            case 401:
                NSLog(@"GET request Error 401 for get supervisors: %@", error);
                [self showErrorMessage:@"Onvoldoende rechten" :operation.responseString];
                break;
                
            case 404:
                NSLog(@"GET request Error 404 for get supervisors: %@", error);
                [self showErrorMessage:@"Niet gevonden" :operation.responseString];
                break;
                
            default:
                NSLog(@"GET request Error for get supervisors: %@", error);
                [self showErrorMessage:@"Fout" :operation.responseString];
                break;
        }
    }];
}

-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    if ([[segue identifier] isEqualToString:@"addline"])
    {
        NewDeclarationLineViewController *declarationlineController =
        [segue destinationViewController];
        
        declarationlineController.declaration = _declaration;
    }
}

- (void) showErrorMessage: (NSString*)errorTitle:(NSString*)errorMessage
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:errorTitle
                                                    message:errorMessage
                                                   delegate:nil
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
}
@end
