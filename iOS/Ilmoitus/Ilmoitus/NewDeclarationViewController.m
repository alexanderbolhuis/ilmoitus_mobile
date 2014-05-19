//
//  SecondViewController.m
//  Ilmoitus
//
//  Created by Alexander Bolhuis on 22-04-14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import "NewDeclarationViewController.h"
#import "Declaration.h"
#import "DeclarationLine.h"
#import "constants.h"

@interface NewDeclarationViewController ()
@property (weak, nonatomic) IBOutlet UIPickerView *supervisorList;
@property (weak, nonatomic) IBOutlet UITextField *supervisor;
@end

@implementation NewDeclarationViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    
    // Create new blank Declaration
    Declaration *newDeclaration = [[Declaration alloc] init];
    [self saveDeclaration];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)saveDeclaration
{
    // ------ FAKE TEST DATA -------------------------------------------------------
    Declaration *decl = [[Declaration alloc] init];
    decl.className = @"open_declaration";
    decl.status = @"Open";
    decl.createdAt = @"2014-05-12 07:27:33.448849";
    decl.createdBy = 4893944777277440;
    NSMutableArray *at = [[NSMutableArray alloc]init];
    [at addObject:[NSNumber numberWithLongLong:6255998092181504]];
    decl.assignedTo = at;
    decl.comment = @"Dit is een test";
    decl.itemsTotalPrice = 30.00;
    decl.itemsCount = 2;
    
    DeclarationLine *line1 = [[DeclarationLine alloc]init];
    line1.cost = 14.00;
    line1.date = @"2014-05-12 07:27:33.448849";
    line1.subtype = 4897217273921536;
    
    DeclarationLine *line2 = [[DeclarationLine alloc]init];
    line2.cost = 16.00;
    line2.date = @"2014-05-05 07:27:33.448849";
    line2.subtype = 4897217273921536;
    
    NSMutableArray *ln = [[NSMutableArray alloc]init];
    [ln addObject:line1];
    [ln addObject:line2];
    decl.lines = ln;
    // ------------------------------------------------------------------------------
    
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    manager.responseSerializer = [AFHTTPResponseSerializer serializer];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:[[NSUserDefaults standardUserDefaults] stringForKey:@"token"] forHTTPHeaderField:@"Authorization"];
    // Declaration
    NSDictionary *declaration = @{@"state":decl.status, @"created_at":decl.createdAt, @"created_by":[NSNumber numberWithLongLong:decl.createdBy], @"assigned_to":decl.assignedTo, @"comment":decl.comment, @"items_total_price":[NSNumber numberWithFloat:decl.itemsTotalPrice], @"items_count":[NSNumber numberWithInt:decl.itemsCount]};
    
    // Lines
    NSMutableArray *declarationlines = [[NSMutableArray alloc] init];
    for (DeclarationLine *line in decl.lines){
        NSDictionary *currentline = @{@"receipt_date": line.date, @"cost":[NSNumber numberWithFloat:line.cost], @"declaration_sub_type":[NSNumber numberWithLongLong:line.subtype]};
        [declarationlines addObject:currentline];
    }
    
    // TODO Attachments
    
    // Total dict
    NSDictionary *params = @{@"declaration":declaration, @"lines":declarationlines, @"attachments":@""};
    
    NSLog(@"POST request parameters: %@",params);
    
    NSString *url = [NSString stringWithFormat:@"%@/declaration", baseURL];
    AFHTTPRequestOperation *apiRequest = [manager POST:url parameters:params success:^(AFHTTPRequestOperation *operation, id responseObject) {
        NSError* error;
        NSDictionary* json = [NSJSONSerialization
                              JSONObjectWithData:responseObject
                              
                              options:kNilOptions
                              error:&error];
        
        NSLog(@"POST request success response: %@",json);
        // Handle success
        
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"POST request Error: %@", error);
        // Handle error

    }];
    
    [apiRequest start];
}

@end
